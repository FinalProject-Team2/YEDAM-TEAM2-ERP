package store.yd2team.business.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import store.yd2team.business.mapper.CustcomMapper;
import store.yd2team.business.service.CustcomService;
import store.yd2team.business.service.CustcomVO;
import store.yd2team.common.util.LoginSession;

@Service
@RequiredArgsConstructor
public class CustcomServiceImpl implements CustcomService {

    private final CustcomMapper custcomMapper;

    // 조회
    @Override
    public List<CustcomVO> searchCustcom(CustcomVO vo) {

        vo.setVendId(LoginSession.getVendId()); // ⭐ 회사코드 세팅

        return custcomMapper.searchCustcom(vo);
    }
	/*
	 * @Override public List<CustcomVO> searchCustcom(CustcomVO vo) { return
	 * custcomMapper.searchCustcom(vo); }
	 */
    
    // 공통코드 조회
    @Override
    public List<CustcomVO> getBSType() {
    	return custcomMapper.selectBSType();
    }
    
    
    // 고객사 등록 + 여신한도 기본 생성
    @Transactional
    @Override
    public int saveNewCust(CustcomVO vo) throws Exception {

        // 1) 세션 정보
        vo.setVendId(LoginSession.getVendId());
        vo.setCreaBy(LoginSession.getEmpId());
        vo.setUpdtBy(LoginSession.getEmpId());

        // 2) 고객사 INSERT
        custcomMapper.insertCustcom(vo);

        // 3) 여신한도 기본 row 생성
        custcomMapper.insertCreditLimit(
            vo.getCustcomId(),
            vo.getVendId()
        );

        return 1;
    }
	/*
	 * // 저장
	 * 
	 * @Transactional
	 * 
	 * @Override public int saveNewCust(CustcomVO vo) throws Exception {
	 * 
	 * // 1) 세션 정보 vo.setVendId(LoginSession.getVendId());
	 * vo.setCreaBy(LoginSession.getEmpId()); vo.setUpdtBy(LoginSession.getEmpId());
	 * 
	 * // 2) 고객사 INSERT custcomMapper.insertCustcom(vo);
	 * 
	 * // 🔥 여기서 custcomId 이미 세팅됨 (selectKey) String custcomId = vo.getCustcomId();
	 * 
	 * // 3) 여신한도 기본 row INSERT custcomMapper.insertCreditLimit(custcomId,
	 * vo.getVendId());
	 * 
	 * return 1; }
	 */
	/*
	 * @Override public int saveNewCust(CustcomVO vo) throws Exception {
	 * System.out.println("### Service saveNewCust 호출 ###"); int result =
	 * custcomMapper.insertCustcom(vo); System.out.println("### result = " +
	 * result); return 1; // 변화 건수 상관없이 성공 처리 }
	 */

}
