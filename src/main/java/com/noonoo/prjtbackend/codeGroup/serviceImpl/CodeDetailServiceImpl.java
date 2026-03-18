package com.noonoo.prjtbackend.codeGroup.serviceImpl;

import com.noonoo.prjtbackend.codeGroup.dto.CodeDetailDto;
import com.noonoo.prjtbackend.codeGroup.dto.CodeDetailSaveRequest;
import com.noonoo.prjtbackend.codeGroup.dto.CodeDetailSearchCondition;
import com.noonoo.prjtbackend.codeGroup.mapper.CodeDetailMapper;
import com.noonoo.prjtbackend.codeGroup.service.CodeDetailService;
import com.noonoo.prjtbackend.common.config.RequestContext;
import com.noonoo.prjtbackend.common.paging.PageResponse;
import com.noonoo.prjtbackend.common.paging.PagingUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CodeDetailServiceImpl implements CodeDetailService {

    private final CodeDetailMapper codeDetailMapper;

    @Override
    public PageResponse<CodeDetailDto> selectList(CodeDetailSearchCondition condition) {
        long totalCount = codeDetailMapper.selectListCnt(condition);
        List<CodeDetailDto> items = codeDetailMapper.selectList(condition);
        return PagingUtils.toPageResponse(condition, totalCount, items);
    }

    @Override
    public CodeDetailDto selectDetail(Long codeDetailSeq) {
        return codeDetailMapper.selectDetail(codeDetailSeq);
    }

    @Override
    @Transactional
    public Map<String, Object> insertData(CodeDetailSaveRequest condition) {
        Map<String, Object> resultMap = new HashMap<>();

        log.info("=======> /api/code-details/create service param={}", condition);

        String loginMemberId = RequestContext.getLoginMemberId();
        String clientIp = RequestContext.getClientIp();

        condition.setCreateId(loginMemberId != null ? loginMemberId : "SYSTEM");
        condition.setModifyId(loginMemberId != null ? loginMemberId : "SYSTEM");
        condition.setCreateIp(clientIp);
        condition.setModifyIp(clientIp);

        if (condition.getUseYn() == null || condition.getUseYn().isBlank()) {
            condition.setUseYn("Y");
        }
        if (condition.getStatus() == null || condition.getStatus().isBlank()) {
            condition.setStatus("ACTIVE");
        }

        int cnt = codeDetailMapper.insertData(condition);

        resultMap.put("status", cnt > 0);
        resultMap.put("msg", cnt > 0 ? "success" : "fail");
        resultMap.put("affectedRows", cnt);

        return resultMap;
    }

    @Override
    @Transactional
    public Map<String, Object> updateData(CodeDetailSaveRequest condition) {
        Map<String, Object> resultMap = new HashMap<>();

        log.info("=======> /api/code-details/update service param={}", condition);

        String loginMemberId = RequestContext.getLoginMemberId();
        String clientIp = RequestContext.getClientIp();

        condition.setModifyId(loginMemberId != null ? loginMemberId : "SYSTEM");
        condition.setModifyIp(clientIp);

        int cnt = codeDetailMapper.updateData(condition);

        resultMap.put("status", cnt > 0);
        resultMap.put("msg", cnt > 0 ? "success" : "fail");
        resultMap.put("affectedRows", cnt);

        return resultMap;
    }

    @Override
    @Transactional
    public Map<String, Object> deleteData(Long codeDetailSeq) {
        Map<String, Object> resultMap = new HashMap<>();

        log.info("=======> /api/code-details/delete service param={}", codeDetailSeq);

        int cnt = codeDetailMapper.deleteData(codeDetailSeq);

        resultMap.put("status", cnt > 0);
        resultMap.put("msg", cnt > 0 ? "success" : "fail");
        resultMap.put("affectedRows", cnt);

        return resultMap;
    }
}