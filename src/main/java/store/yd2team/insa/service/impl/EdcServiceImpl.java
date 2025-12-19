package store.yd2team.insa.service.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import store.yd2team.common.mapper.CodeMapper;
import store.yd2team.common.service.CodeVO;
import store.yd2team.insa.mapper.DeptMapper;
import store.yd2team.insa.mapper.EdcMapper;
import store.yd2team.insa.mapper.VcatnMapper;
import store.yd2team.insa.mapper.WkTyMapper;
import store.yd2team.insa.service.DeptVO;
import store.yd2team.insa.service.EdcService;
import store.yd2team.insa.service.EdcVO;
import store.yd2team.insa.service.EmpVO;
import store.yd2team.insa.service.HldyWkBasiVO;
import store.yd2team.common.util.LoginSession;
@Service
@RequiredArgsConstructor
public class EdcServiceImpl implements EdcService{

	private final EdcMapper edcMapper;
	private final CodeMapper codeMapper;
	private final DeptMapper deptMapper;
	private final WkTyMapper wkTyMapper;	
	
	@Override
	public List<EdcVO> getListEdcJohoe(EdcVO edc) {
		
		return edcMapper.getListEdcJohoe(edc);
	}

	@Override
	public List<EdcVO> getListEdcDetaJohoe(EdcVO edc) {
		
		return edcMapper.getListEdcDetaJohoe(edc);
	}

	@Override
	public Map<String, Object> getInputOption() {
		Map<String, Object> result = new HashMap<>();
		
		CodeVO grpId = new CodeVO();
		String vendVal = LoginSession.getVendId();
		grpId.setGrpId("k");
		grpId.setVendId( vendVal );
		List<CodeVO> gradeList = codeMapper.findCode(grpId);
		result.put("grade", gradeList); //직급
		grpId.setGrpId("l");
		List<CodeVO> titleList = codeMapper.findCode(grpId);
		result.put("title", titleList); //직책
		grpId.setGrpId("m");
		List<CodeVO> emplymTyList = codeMapper.findCode(grpId);
		result.put("emplymTyLi", emplymTyList); //고용형태
		grpId.setGrpId("n");
		List<CodeVO> hffcStList = codeMapper.findCode(grpId);
		result.put("hffcStLi", hffcStList); //재직상태
		List<DeptVO> deptList = deptMapper.getListDept( vendVal );
		result.put("dept", deptList);
		HldyWkBasiVO vendId = new HldyWkBasiVO();
		vendId.setVendId( vendVal );
		List<HldyWkBasiVO> selectHldyWkBasiList = wkTyMapper.selectHldyWkBasiList( vendId );
		result.put("basiLi", selectHldyWkBasiList);

		return result;
	}

	@Transactional
	@Override
	public int setDbEdcAdd(EdcVO edc) {
		//edc Id생성
		edc.setEdcId(edcMapper.setDbEdcAddId());
		edc.setVendId( LoginSession.getVendId() ); //임시로 넣는값 나중에 거래처 로그인하게되면 세선에서 받아올거임
		edcMapper.setDbEdcAdd(edc);
		EmpVO empVal = new EmpVO();	
		System.out.println( "값이 뭔지 보여줘"+edc.getEdcSel().charAt(0) );
		empVal.setVendId( LoginSession.getVendId() );
		switch (edc.getEdcSel().charAt(0)) {
				    case 'a' -> empVal.setVendId( LoginSession.getVendId() );
				    case 'D' -> empVal.setDeptId(edc.getEdcSel());
				    case 'k' -> empVal.setClsf(edc.getEdcSel());
				    default  -> empVal.setRspofc(edc.getEdcSel());
				}
		List<EdcVO> gradeList = edcMapper.getEdcPickList(empVal);
		String baseId = edcMapper.setDbEdcTTAddId();
		int seq = Integer.parseInt(baseId.substring(baseId.length() - 3)); // 마지막 3자리 숫자

		for (int i = 0; i < gradeList.size(); i++) {
		    EdcVO vo = gradeList.get(i);
		    System.out.println("몇번째행인지 보여조"+vo);
		    String newId = "edcTT" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyMM"))
		                 + String.format("%03d", seq + i);
		    vo.setEdcTrgterId(newId);
		    vo.setEdcId(edc.getEdcId()); // FK로 edcId도 같이 세팅
		}		
		


		return edcMapper.insertEdcTrgterList(gradeList);
	}

	@Override
	public List<EdcVO> getListEdcUserJohoe(EdcVO edc) {
		
		return edcMapper.getListEdcUserJohoe(edc);
	}

	@Override
	public int edcUserRegistChk(EdcVO edc) {
		
		return edcMapper.edcUserRegistChk(edc);
	}

}
