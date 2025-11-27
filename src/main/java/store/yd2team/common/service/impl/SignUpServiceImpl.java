package store.yd2team.common.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import store.yd2team.common.mapper.SignUpMapper;
import store.yd2team.common.service.SignUpService;

@Service
public class SignUpServiceImpl implements SignUpService {
		
	@Autowired
	SignUpMapper signUpMapper;

}// end class