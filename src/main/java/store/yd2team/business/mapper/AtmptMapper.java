package store.yd2team.business.mapper;

import java.util.List;

import store.yd2team.business.service.AtmptVO;

public interface AtmptMapper {

	static List<AtmptVO> searchCustcomId(String keyword) {
		return null;
	}

	static List<AtmptVO> searchCustcomName(String keyword) {
		return null;
	}

	int insertAtmpt(AtmptVO vo);

	static List<AtmptVO> searchAtmpt(AtmptVO vo) {
		return null;
	}

}
