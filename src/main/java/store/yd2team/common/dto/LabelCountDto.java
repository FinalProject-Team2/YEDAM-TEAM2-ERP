package store.yd2team.common.dto;

import lombok.Data;

@Data
public class LabelCountDto {
    private String label; // st 또는 role_ty
    private int cnt;
}
