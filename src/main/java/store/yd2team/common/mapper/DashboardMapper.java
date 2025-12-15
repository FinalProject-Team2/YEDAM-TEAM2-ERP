package store.yd2team.common.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import store.yd2team.common.dto.DashboardSummaryDto;
import store.yd2team.common.dto.LabelCountDto;
import store.yd2team.common.dto.RecentAuthChangeDto;
import store.yd2team.common.dto.TopLoginFailDto;

@Mapper
public interface DashboardMapper {

    DashboardSummaryDto selectSummary(@Param("vendId") String vendId);

    List<LabelCountDto> selectAcctStatusDist(@Param("vendId") String vendId); // tb_emp_acct.st 분포
    List<LabelCountDto> selectRoleTypeCount(@Param("vendId") String vendId);  // tb_role.role_ty 분포

    List<TopLoginFailDto> selectTopLoginFail(@Param("vendId") String vendId);
    List<RecentAuthChangeDto> selectRecentAuthChanges(@Param("vendId") String vendId);
}
