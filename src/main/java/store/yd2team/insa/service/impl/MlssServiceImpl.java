package store.yd2team.insa.service.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import store.yd2team.common.util.LoginSession;
import store.yd2team.insa.mapper.MlssMapper;
import store.yd2team.insa.service.MlssService;
import store.yd2team.insa.service.MlssVO;


@Service
@RequiredArgsConstructor
public class MlssServiceImpl implements MlssService{
	
	private final MlssMapper mlssMapper;
	
	@Override
	public int mlssRegist(MlssVO val) {
		
		val.setVendId( LoginSession.getVendId() );		
		
		List<MlssVO> empIdList = mlssMapper.empIdList(val);
		String baseId = mlssMapper.mlssCreateId();
		int seq = Integer.parseInt(baseId.substring(baseId.length() - 3)); // 마지막 3자리 숫자
		
		for (int i = 0; i < empIdList.size(); i++) {
			MlssVO vo = empIdList.get(i);			
			vo.setMlssNm( val.getMlssNm() );
			vo.setEvlBeginDt( val.getEvlBeginDt() );
			vo.setEvlEndDt( val.getEvlEndDt() );
			vo.setCreaDt( val.getCreaDt() );
			String newId = "mlss" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyMM"))
	                 + String.format("%03d", seq + i);
			vo.setMlssId(newId);
		}
		
		return mlssMapper.insertMlssList(empIdList);
	}

}
