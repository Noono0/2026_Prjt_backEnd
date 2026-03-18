package com.noonoo.prjtbackend.codeGroup.serviceImpl;

import com.noonoo.prjtbackend.codeGroup.dto.CodeGroupDto;
import com.noonoo.prjtbackend.codeGroup.dto.CodeGroupSaveRequest;
import com.noonoo.prjtbackend.codeGroup.dto.CodeGroupSearchCondition;
import com.noonoo.prjtbackend.codeGroup.dto.OptionDto;
import com.noonoo.prjtbackend.codeGroup.mapper.CodeGroupsMapper;
import com.noonoo.prjtbackend.codeGroup.service.CodeGroupService;
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
public class CodeGroupServiceImpl implements CodeGroupService {

    private final CodeGroupsMapper codeGroupsMapper;

    @Override
    public PageResponse<CodeGroupDto> selectList(CodeGroupSearchCondition condition) {
        long totalCount = codeGroupsMapper.selectListCnt(condition);
        List<CodeGroupDto> items = codeGroupsMapper.selectList(condition);
        return PagingUtils.toPageResponse(condition, totalCount, items);
    }

    @Override
    public CodeGroupDto selectDetail(Long codeGroupSeq) {
        return codeGroupsMapper.selectDetail(codeGroupSeq);
    }

    @Override
    public CodeGroupDto findIdCheck(CodeGroupSaveRequest condition) {
        return codeGroupsMapper.findIdCheck(condition);
    }

    @Override
    @Transactional
    public Map<String, Object> insertData(CodeGroupSaveRequest condition) {
        Map<String, Object> resultMap = new HashMap<>();

        log.info("=======> /api/code-groups/create service param={}", condition);

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

        int cnt = codeGroupsMapper.insertData(condition);

        resultMap.put("status", cnt > 0);
        resultMap.put("msg", cnt > 0 ? "success" : "fail");
        resultMap.put("affectedRows", cnt);

        return resultMap;
    }

    @Override
    @Transactional
    public Map<String, Object> updateData(CodeGroupSaveRequest condition) {
        Map<String, Object> resultMap = new HashMap<>();

        log.info("=======> /api/code-groups/update service param={}", condition);

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

        int cnt = codeGroupsMapper.updateData(condition);

        resultMap.put("status", cnt > 0);
        resultMap.put("msg", cnt > 0 ? "success" : "fail");
        resultMap.put("affectedRows", cnt);

        return resultMap;
    }

    @Override
    @Transactional
    public Map<String, Object> deleteData(Long codeGroupSeq) {
        Map<String, Object> resultMap = new HashMap<>();

        log.info("=======> /api/code-groups/delete service param={}", codeGroupSeq);

        int cnt = codeGroupsMapper.deleteData(codeGroupSeq);

        resultMap.put("status", cnt > 0);
        resultMap.put("msg", cnt > 0 ? "success" : "fail");
        resultMap.put("affectedRows", cnt);

        return resultMap;
    }

    @Override
    public List<OptionDto> findCodeGroupOptions() {
        return codeGroupsMapper.findCodeGroupOptions();
    }
}