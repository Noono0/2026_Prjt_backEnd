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

import java.util.List;

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
    public CodeDetailDto selectDetail(CodeDetailSearchCondition condition) {
        return codeDetailMapper.selectDetail(condition);
    }

    @Override
    @Transactional
    public int insertData(CodeDetailSaveRequest condition) {
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
        if (condition.getSortOrder() == null) {
            condition.setSortOrder(0);
        }

        return codeDetailMapper.insertData(condition);
    }

    @Override
    @Transactional
    public int updateData(CodeDetailSaveRequest condition) {
        log.info("=======> /api/code-details/update service param={}", condition);

        String loginMemberId = RequestContext.getLoginMemberId();
        String clientIp = RequestContext.getClientIp();

        condition.setModifyId(loginMemberId != null ? loginMemberId : "SYSTEM");
        condition.setModifyIp(clientIp);

        if (condition.getUseYn() == null || condition.getUseYn().isBlank()) {
            condition.setUseYn("Y");
        }
        if (condition.getSortOrder() == null) {
            condition.setSortOrder(0);
        }

        return codeDetailMapper.updateData(condition);
    }

    @Override
    @Transactional
    public int deleteData(CodeDetailSaveRequest condition) {
        log.info("=======> /api/code-details/delete service param={}", condition);

        String loginMemberId = RequestContext.getLoginMemberId();
        String clientIp = RequestContext.getClientIp();

        condition.setModifyId(loginMemberId != null ? loginMemberId : "SYSTEM");
        condition.setModifyIp(clientIp);

        return codeDetailMapper.deleteData(condition);
    }
}