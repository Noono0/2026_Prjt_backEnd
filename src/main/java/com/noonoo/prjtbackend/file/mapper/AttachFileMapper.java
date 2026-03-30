package com.noonoo.prjtbackend.file.mapper;

import com.noonoo.prjtbackend.file.dto.AttachFileDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AttachFileMapper {

    int insertAttachFile(AttachFileDto row);

    AttachFileDto selectByFileSeq(@Param("fileSeq") Long fileSeq);
}
