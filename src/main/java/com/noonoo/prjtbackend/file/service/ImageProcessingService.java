package com.noonoo.prjtbackend.file.service;

import com.noonoo.prjtbackend.file.dto.ImageProcessResult;
import com.noonoo.prjtbackend.file.exception.ImageDecodeException;
import com.noonoo.prjtbackend.file.model.ImageUploadPurpose;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;
import java.util.Objects;

/**
 * multipart 한도 안에서 받은 원본 바이트를 웹용으로 리사이즈·압축한다.
 * GIF 는 첫 프레임만 디코드되면 정지 이미지로 변환된다.
 */
@Service
public class ImageProcessingService {

    private static final String NOTICE =
            "이미지가 커서 서버에서 웹에 맞게 크기·용량을 줄여 저장했습니다.";

    @Value("${app.file.image.optimize-enabled:true}")
    private boolean optimizeEnabled;

    @Value("${app.file.image.board.max-width-px:1600}")
    private int boardMaxWidthPx;

    @Value("${app.file.image.board.target-min-bytes:524288}")
    private int boardTargetMinBytes;

    @Value("${app.file.image.board.target-max-bytes:1048576}")
    private int boardTargetMaxBytes;

    @Value("${app.file.image.profile.max-edge-px:512}")
    private int profileMaxEdgePx;

    @Value("${app.file.image.profile.target-min-bytes:102400}")
    private int profileTargetMinBytes;

    @Value("${app.file.image.profile.target-max-bytes:307200}")
    private int profileTargetMaxBytes;

    @Value("${app.file.image.preferred-raster-format:jpeg}")
    private String preferredRasterFormat;

    @Value("${app.file.image.png-keep-alpha-as-png:true}")
    private boolean pngKeepAlphaAsPng;

    public ImageProcessResult process(
            byte[] raw,
            String normalizedContentType,
            String originalExt,
            ImageUploadPurpose purpose
    ) {
        Objects.requireNonNull(raw, "raw");
        String nct = normalizedContentType.toLowerCase(Locale.ROOT);
        String oext = originalExt == null ? "" : originalExt.toLowerCase(Locale.ROOT);

        if (!optimizeEnabled) {
            return new ImageProcessResult(
                    raw,
                    nct,
                    extensionFromMime(nct),
                    false,
                    null
            );
        }

        BufferedImage src;
        try {
            src = ImageIO.read(new ByteArrayInputStream(raw));
        } catch (IOException e) {
            throw new ImageDecodeException("이미지를 읽는 중 오류가 발생했습니다.", e);
        }
        if (src == null) {
            throw new ImageDecodeException("이미지 디코드에 실패했습니다. 지원 형식이거나 손상 여부를 확인해 주세요.");
        }

        int origW = src.getWidth();
        int origH = src.getHeight();
        boolean alpha = src.getColorModel() != null && src.getColorModel().hasAlpha();
        boolean declaredPng = "image/png".equals(nct) || "png".equals(oext);

        BufferedImage geometry = resizeForPurpose(src, purpose);
        boolean geometryReduced = geometry.getWidth() != origW || geometry.getHeight() != origH;

        if (alpha && pngKeepAlphaAsPng && declaredPng) {
            int maxB = purpose == ImageUploadPurpose.PROFILE ? profileTargetMaxBytes : boardTargetMaxBytes;
            byte[] pngBytes = encodePngUnderMaxBytes(geometry, maxB);
            boolean down = geometryReduced || pngBytes.length < raw.length * 0.85;
            return new ImageProcessResult(
                    pngBytes,
                    "image/png",
                    "png",
                    down,
                    down ? NOTICE : null
            );
        }

        BufferedImage rgb = flattenToRgb(geometry);
        int minB = purpose == ImageUploadPurpose.PROFILE ? profileTargetMinBytes : boardTargetMinBytes;
        int maxB = purpose == ImageUploadPurpose.PROFILE ? profileTargetMaxBytes : boardTargetMaxBytes;

        boolean wantWebp = "webp".equalsIgnoreCase(preferredRasterFormat.trim());
        RasterPack pack = encodeRasterInRange(rgb, minB, maxB, wantWebp);

        boolean formatToJpeg = "jpg".equals(pack.extension) && (declaredPng || "image/webp".equals(nct) || "webp".equals(oext));
        boolean down = geometryReduced
                || pack.bytes.length < raw.length * 0.85
                || formatToJpeg
                || "gif".equals(oext)
                || "image/gif".equals(nct);

        return new ImageProcessResult(
                pack.bytes,
                pack.contentType,
                pack.extension,
                down,
                down ? NOTICE : null
        );
    }

    private BufferedImage resizeForPurpose(BufferedImage src, ImageUploadPurpose purpose) {
        try {
            if (purpose == ImageUploadPurpose.PROFILE) {
                if (profileMaxEdgePx <= 0) {
                    return src;
                }
                int w = src.getWidth();
                int h = src.getHeight();
                if (Math.max(w, h) <= profileMaxEdgePx) {
                    return src;
                }
                BufferedImage out = Thumbnails.of(src)
                        .size(profileMaxEdgePx, profileMaxEdgePx)
                        .keepAspectRatio(true)
                        .asBufferedImage();
                return out != null ? out : src;
            }
            if (boardMaxWidthPx <= 0) {
                return src;
            }
            if (src.getWidth() <= boardMaxWidthPx) {
                return src;
            }
            BufferedImage out = Thumbnails.of(src)
                    .width(boardMaxWidthPx)
                    .keepAspectRatio(true)
                    .asBufferedImage();
            return out != null ? out : src;
        } catch (IOException e) {
            throw new ImageDecodeException("이미지 리사이즈에 실패했습니다.", e);
        }
    }

    private static BufferedImage flattenToRgb(BufferedImage src) {
        if (src.getType() == BufferedImage.TYPE_INT_RGB) {
            return src;
        }
        BufferedImage rgb = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = rgb.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(src, 0, 0, null);
        g.dispose();
        return rgb;
    }

    private byte[] encodePngUnderMaxBytes(BufferedImage img, int maxBytes) throws ImageDecodeException {
        try {
            BufferedImage current = img;
            for (int i = 0; i < 12; i++) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                if (!ImageIO.write(current, "png", baos)) {
                    throw new ImageDecodeException("PNG 인코딩에 실패했습니다.");
                }
                byte[] data = baos.toByteArray();
                if (data.length <= maxBytes) {
                    return data;
                }
                BufferedImage smaller = scaleImage(current, 0.88);
                if (smaller.getWidth() == current.getWidth() && smaller.getHeight() == current.getHeight()) {
                    return data;
                }
                current = smaller;
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(current, "png", baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new ImageDecodeException("PNG 처리 중 오류가 발생했습니다.", e);
        }
    }

    private RasterPack encodeRasterInRange(BufferedImage rgb, int minBytes, int maxBytes, boolean preferWebp) {
        try {
            if (preferWebp && hasWebpWriter()) {
                RasterPack w = tryFitWebp(rgb, minBytes, maxBytes);
                if (w != null) {
                    return w;
                }
            }
            return fitJpeg(rgb, minBytes, maxBytes);
        } catch (IOException e) {
            throw new ImageDecodeException("이미지 인코딩에 실패했습니다.", e);
        }
    }

    private static boolean hasWebpWriter() {
        return ImageIO.getImageWritersByFormatName("webp").hasNext();
    }

    private RasterPack tryFitWebp(BufferedImage rgb, int minBytes, int maxBytes) throws IOException {
        if (!hasWebpWriter()) {
            return null;
        }
        BufferedImage img = rgb;
        float q = 0.88f;
        int round = 0;
        while (true) {
            byte[] out = encodeWebpOnce(img, q);
            if (out == null) {
                return null;
            }
            if (out.length <= maxBytes && out.length >= minBytes) {
                return new RasterPack(out, "image/webp", "webp");
            }
            if (out.length > maxBytes) {
                if (q > 0.38f) {
                    q -= 0.05f;
                    continue;
                }
                if (round < 10) {
                    img = scaleImage(img, 0.9);
                    round++;
                    q = 0.85f;
                    continue;
                }
                return new RasterPack(out, "image/webp", "webp");
            }
            if (q < 0.93f) {
                q += 0.03f;
                continue;
            }
            return new RasterPack(out, "image/webp", "webp");
        }
    }

    private static byte[] encodeWebpOnce(BufferedImage img, float q) throws IOException {
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("webp");
        if (!writers.hasNext()) {
            return null;
        }
        ImageWriter writer = writers.next();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ImageOutputStream ios = ImageIO.createImageOutputStream(baos)) {
            writer.setOutput(ios);
            ImageWriteParam param = writer.getDefaultWriteParam();
            if (param.canWriteCompressed()) {
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(Math.min(1f, Math.max(0.05f, q)));
            }
            writer.write(null, new IIOImage(img, null, null), param);
            return baos.toByteArray();
        } catch (Exception e) {
            return null;
        } finally {
            writer.dispose();
        }
    }

    private RasterPack fitJpeg(BufferedImage rgb, int minBytes, int maxBytes) throws IOException {
        BufferedImage img = rgb;
        float q = 0.88f;
        int round = 0;
        while (true) {
            byte[] out = encodeJpeg(img, q);
            if (out.length <= maxBytes && out.length >= minBytes) {
                return new RasterPack(out, "image/jpeg", "jpg");
            }
            if (out.length > maxBytes) {
                if (q > 0.36f) {
                    q -= 0.04f;
                    continue;
                }
                if (round < 10) {
                    img = scaleImage(img, 0.9);
                    round++;
                    q = 0.85f;
                    continue;
                }
                return new RasterPack(out, "image/jpeg", "jpg");
            }
            if (q < 0.94f) {
                q += 0.03f;
                continue;
            }
            return new RasterPack(out, "image/jpeg", "jpg");
        }
    }

    private static byte[] encodeJpeg(BufferedImage rgb, float quality) throws IOException {
        float q = Math.min(1f, Math.max(0.05f, quality));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
        if (!writers.hasNext()) {
            Thumbnails.of(rgb).scale(1.0).outputFormat("jpg").outputQuality(q).toOutputStream(baos);
            return baos.toByteArray();
        }
        ImageWriter writer = writers.next();
        try (ImageOutputStream ios = ImageIO.createImageOutputStream(baos)) {
            writer.setOutput(ios);
            ImageWriteParam param = writer.getDefaultWriteParam();
            if (param.canWriteCompressed()) {
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(q);
            }
            writer.write(null, new IIOImage(rgb, null, null), param);
        } finally {
            writer.dispose();
        }
        return baos.toByteArray();
    }

    private static BufferedImage scaleImage(BufferedImage img, double factor) throws IOException {
        if (factor >= 0.999) {
            return img;
        }
        BufferedImage out = Thumbnails.of(img).scale(factor).asBufferedImage();
        return out != null ? out : img;
    }

    private static String extensionFromMime(String nct) {
        return switch (nct) {
            case "image/jpeg" -> "jpg";
            case "image/png" -> "png";
            case "image/webp" -> "webp";
            case "image/gif" -> "gif";
            default -> "bin";
        };
    }

    private record RasterPack(byte[] bytes, String contentType, String extension) {}
}
