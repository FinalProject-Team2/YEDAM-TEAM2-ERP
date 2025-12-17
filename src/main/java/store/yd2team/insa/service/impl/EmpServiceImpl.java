package store.yd2team.insa.service.impl;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import store.yd2team.common.mapper.EmpAcctMapper;
import store.yd2team.insa.mapper.DeptMapper;
import store.yd2team.insa.mapper.EmpMapper;
import store.yd2team.insa.mapper.VcatnMapper;
import store.yd2team.insa.service.EmpService;
import store.yd2team.insa.service.EmpVO;
import store.yd2team.insa.service.YrycVO;

@Service
@RequiredArgsConstructor
public class EmpServiceImpl implements EmpService{
	
	private final EmpMapper empMapper;
	private final VcatnMapper vcatnMapper;
	private final DeptMapper deptMapper;
	private final EmpAcctMapper empAcctMapper;
	
	@Override
	public List<EmpVO> getListEmpJohoe(EmpVO emp) {
		
		return empMapper.getListEmpJohoe(emp);
	}

	@Override
	@Transactional
	public int setDbEdit(EmpVO emp) {
		
        int updated = empMapper.setDbEdit(emp);
        String updtBy = emp.getUpdtBy();

        String hffcSt = emp.getHffcSt();
        
        if ("n2".equals(hffcSt) || "n3".equals(hffcSt)) {
            // 퇴사/휴직(가정) 등: 계정 비활성
            empAcctMapper.updateAcctStatusToInactive(emp.getVendId(), emp.getEmpId(), updtBy); 
        } else {
            empAcctMapper.updateAcctStatusToActive(emp.getVendId(), emp.getEmpId(), updtBy); 
        }
        
        return updated;
    }

	@Override
	public EmpVO setDbAddId() {
		
		return empMapper.setDbAddId();
	}
	
	@Override
	public int setDbAdd(EmpVO emp) {
		YrycVO yrycvo = new YrycVO();
		//VcatnYear에 세팅할 데이터가공 코드
		Date encpnDate = emp.getEncpn();
		LocalDate localDate = encpnDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        int year = localDate.getYear(); // 바로 int로 추출
		yrycvo.setVcatnYear(year);
		yrycvo.setYrycExtshDt(encpnDate);
		vcatnMapper.empYrycRegist(yrycvo);
		return empMapper.setDbAdd(emp);
	}

	@Override
	public List<EmpVO> getOrgRenderList(String val) {
		
		return deptMapper.getOrgRenderList(val);
	}

}
