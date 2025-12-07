package store.yd2team.insa.service;



import java.util.List;

import lombok.Data;

@Data
public class MlssRequestVO {

	private MlssVO master;
    private List<MlssVO> datas;
}
