package store.yd2team.insa.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import store.yd2team.insa.mapper.VcatnMapper;
import store.yd2team.insa.service.VcatnService;
import store.yd2team.insa.service.VcatnVO;
@Service
@RequiredArgsConstructor
public class VcatnServiceImpl implements VcatnService{

	private final VcatnMapper vcatnMapper;
	
	//날짜를 밀리초에서 "일"로 바꾸는 메서드
			private int vcatnDate(Date begin, Date end) {
				long b = begin.getTime() / (1000 * 60 * 60 * 24);
				long e = end.getTime() / (1000 * 60 * 60 * 24);
				long v = 0;
				if(e-b == 0) {v=1;}else{v=1+e-b;}
				return (int)v;
			}
	
	@Override
	public List<VcatnVO> vcatnListVo(VcatnVO val) {
		//휴가 다중검색조회 쿼리(사용자 및 관리자 유동쿼리)
		return vcatnMapper.getListVcatnJohoe(val);
	}

	@Override
	public String vcatnRegist(VcatnVO val) {
		
		int vcatnDe = vcatnDate(val.getVcatnBegin(), val.getVcatnEnd() );	
		int remndrNum = vcatnMapper.yrycUserRemndrChk( val.getEmpId() );
		if(remndrNum<vcatnDe) {
			return "잔여연차보다 사용연차가 더 많습니다.";
		}
		if(vcatnDe < 0) {
			return "날짜를 다시 선택해 주세요.";
		}
		val.setVcatnDe( (double)vcatnDe );
		vcatnMapper.vcatnCreateData(val);
		vcatnMapper.vcatnUpdateYrycData(val.getEmpId(), vcatnDe);
		return "승인확인 후 연차 진행하세요!";
	}

	@Override
	public int yrycUserRemndrChk(String val) {
		
		return vcatnMapper.yrycUserRemndrChk(val);
	}

	@Transactional
	@Override
	public int vcatnRollback(VcatnVO val) {
		vcatnMapper.vcateDel(val);
		vcatnMapper.yrycRollback(val);
		return 1;
	}

	@Override
	public int vcatnCfmUpdate(VcatnVO val) {
		
		return vcatnMapper.vcatnCfmUpdate(val);
	}

}
