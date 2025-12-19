package store.yd2team.insa.service.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import store.yd2team.common.util.LoginSession;
import store.yd2team.insa.mapper.MlssMapper;
import store.yd2team.insa.service.MlssRequestVO;
import store.yd2team.insa.service.MlssService;
import store.yd2team.insa.service.MlssVO;


@Service
@RequiredArgsConstructor
public class MlssServiceImpl implements MlssService{
	
	private final MlssMapper mlssMapper;
	
	
	@Transactional
	@Override
	public int mlssRegist(MlssVO val) {
		
		val.setVendId( LoginSession.getVendId() );		
		
		List<MlssVO> empIdList = mlssMapper.empIdList(val);
		String baseId = mlssMapper.mlssCreateId();
		int seq = Integer.parseInt(baseId.substring(baseId.length() - 3)); // 마지막 3자리 숫자
		
		//헤더등록.
		MlssVO mvo = new MlssVO();
		mvo.setVendId( LoginSession.getVendId() );
		mvo.setMlssNm( val.getMlssNm() );
		mvo.setEvlBeginDt( val.getEvlBeginDt() );
		mvo.setEvlEndDt( val.getEvlEndDt() );
		
		mlssMapper.insertMlssHead(mvo);
		
		String ym = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMM"));
		for (int i = 0; i < empIdList.size(); i++) {
			MlssVO vo = empIdList.get(i);
			vo.setMlssId(mvo.getMlssId());
			vo.setCreaDt( val.getCreaDt() );
			String newId = "mlss_emp" + ym  + String.format("%03d", seq + i);
			vo.setMlssEmpId(newId);
		}
		
		//
		return mlssMapper.insertMlssList(empIdList);
	}

	@Override
	public List<MlssVO> mlssListJohoe(MlssVO val) {
		
		return mlssMapper.mlssSearchList(val);
	}

	@Override
	public Map<String, List<MlssVO>> mlssLoadBefore() {
		Map<String, List<MlssVO>> result = new HashMap<>();
		String empId = LoginSession.getEmpId();
		//평가항목 다 불러오기
		List<MlssVO> iemList = mlssMapper.mlssIemList();
		
		for (MlssVO vo : iemList) {
		    String ability = vo.getAbility();

		    // 해당 ability 키가 없으면 새 리스트 생성
		    result.putIfAbsent(ability, new ArrayList<>());

		    // 해당 ability 리스트에 vo 추가
		    result.get(ability).add(vo);
		}

		// 확인용 출력
		for (Map.Entry<String, List<MlssVO>> entry : result.entrySet()) {
		    System.out.println("Ability: " + entry.getKey());
		    for (MlssVO vo : entry.getValue()) {
		        System.out.println("  - " + vo.getMlssIem());
		    }
		}

		//내꺼다면평가 등록된 건 조회
		
		return result;
	}

	@Override
	public MlssVO mlssVisitChk(String val) {		
		return mlssMapper.mlssDtChk(val);
	}

	@Override
	public int mlssWrterRegist(MlssRequestVO val) {
		String wrterId = val.getMaster().getMlssWrterId();
		if(wrterId == null) {
			mlssMapper.mlssWrterRegist( val.getDatas() );
			mlssMapper.mlssMasterUpdate( val.getMaster() );
			return 0;
		}
		
		return 0;
	}

	@Override
	public List<MlssVO> mlssEmpList(String empId, String deptId) {
		
		return mlssMapper.mlssEmpList(empId, deptId);
	}

	@Override
	public List<MlssVO> mlssWrterLoadBefore(String mlssId, String empId) {
		
		return mlssMapper.mlssWrterLoadBefore(mlssId, empId);
	}

	@Override
	public List<MlssVO> mlssStLoadBefore(String vendId, String deptId, String mlssId, String empId) {
		
		return mlssMapper.mlssStLoadBefore(vendId, deptId, mlssId, empId);
	}

	@Override
	public List<MlssVO> FinalResultMlssList(String vendId, String deptId, String mlssId, String empId) {
		List<MlssVO> vo = mlssMapper.FinalResultMlssList(vendId, deptId, mlssId, empId);
		List<MlssVO> vo2 = mlssMapper.FinalResultMlssEtcList(vendId, deptId, mlssId, empId);
		vo.addAll(vo2);
		return vo;
	}

}
