package store.yd2team.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PwChangeResultDto {

    private boolean success;
    private String message;

    public static PwChangeResultDto ok(String message) {
        return new PwChangeResultDto(true, message);
    }

    public static PwChangeResultDto fail(String message) {
        return new PwChangeResultDto(false, message);
    }

}
