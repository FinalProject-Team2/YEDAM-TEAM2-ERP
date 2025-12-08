package store.yd2team.common.dto;

import java.util.List;

import lombok.Data;
import store.yd2team.common.service.RoleVO;

@Data
public class RoleSaveRequest {

    private List<RoleVO> created;
    private List<RoleVO> updated;
}
