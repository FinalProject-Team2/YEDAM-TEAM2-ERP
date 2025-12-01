package store.yd2team.common.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import store.yd2team.common.mapper.SignUpMapper;
import store.yd2team.common.service.SignUpService;

@Service
public class SignUpServiceImpl implements SignUpService {
		
	@Autowired
	SignUpMapper signUpMapper;
	
	// 아이디 중복체크
	@Override
	public boolean isLoginIdDuplicated(String loginId) {
		if (loginId == null || loginId.isBlank()) {
			return false;
		}
		int count = signUpMapper.countLoginId(loginId);
		return count > 0;
	}
	
	// 사업자등록번호 중복체크
	@Override
	public boolean isBizNoDuplicated(String bizNo) {
		if (bizNo == null || bizNo.isBlank()) {
			return false;
		}
		int count = signUpMapper.countBizNo(bizNo);
		return count > 0;
	}

}// end class