package com.noonoo.prjtbackend.member.mapper;

import com.noonoo.prjtbackend.member.dto.PointPolicyRowDto;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PointPolicyMapper {

    List<PointPolicyRowDto> selectAllPolicies();

    PointPolicyRowDto selectByPolicyKey(@Param("policyKey") String policyKey);

    int updatePolicy(PointPolicyRowDto row);

    /**
     * 아직 지급 이력이 없을 때만 1행 삽입. 삽입된 행이 있으면 1, 이미 있으면 0.
     */
    int insertMilestoneGrantedIfAbsent(
            @Param("boardSeq") long boardSeq,
            @Param("writerMemberSeq") long writerMemberSeq,
            @Param("rewardPoints") long rewardPoints);

    int deleteMilestoneGranted(@Param("boardSeq") long boardSeq);
}
