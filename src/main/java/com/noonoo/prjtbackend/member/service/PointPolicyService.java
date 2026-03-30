package com.noonoo.prjtbackend.member.service;

import com.noonoo.prjtbackend.board.dto.BoardDto;
import com.noonoo.prjtbackend.member.dto.PointPolicyRowDto;
import java.util.List;

public interface PointPolicyService {

    List<PointPolicyRowDto> listAllPolicies();

    void saveAllPolicies(List<PointPolicyRowDto> rows);

    /** 게시글 추천 반영 직후 호출: 자유게시판·임계값·미지급이면 작성자 보상 */
    void tryGrantFreeBoardLikeMilestone(BoardDto boardAfterLike);
}
