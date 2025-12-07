package store.yd2team.business.service;

import java.util.List;

public interface AtmptService {

	List<AtmptVO> searchAtmpt(AtmptVO searchVO);

	List<AtmptVO> searchCustcomId(String keyword);

	List<AtmptVO> searchCustcomName(String keyword);


}
