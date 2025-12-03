package store.yd2team.common.service.impl;

import org.springframework.stereotype.Service;

import static store.yd2team.common.consts.CodeConst.Yn.*;

import lombok.RequiredArgsConstructor;
import store.yd2team.common.mapper.SecPolicyMapper;
import store.yd2team.common.service.SecPolicyService;
import store.yd2team.common.service.SecPolicyVO;

@Service
@RequiredArgsConstructor
public class SecPolicyServiceImpl implements SecPolicyService {

    private final SecPolicyMapper secPolicyMapper;
    
    @Override
    public boolean existsForVend(String vendId) {
        return secPolicyMapper.countByVendId(vendId) > 0;
    }

    
    @Override
    public SecPolicyVO getByVendIdOrDefault(String vendId) {

        SecPolicyVO vo = secPolicyMapper.selectByVendId(vendId);

        // tb_sec_policy 에 row 자체가 하나도 없을 때 기본 정책 사용
        if (vo == null) {
            vo = SecPolicyVO.defaultPolicy();
        }

        return vo;
    }

    @Override
    public SecPolicyVO saveForVend(String vendId, String empId, SecPolicyVO reqVo) {
    	
    	validatePolicy(reqVo);
    	
        // DB에 실제로 저장할 VO 구성
        SecPolicyVO vo = new SecPolicyVO();
        vo.setVendId(vendId);
        vo.setPolicyId(vendId); // 심플하게 vendId = policyId

        applyFromRequest(reqVo, vo);

        int count = secPolicyMapper.countByVendId(vendId);

        if (count > 0) {
            // 존재하면 UPDATE → updt_by만 사용
            vo.setUpdtBy(empId);
            secPolicyMapper.updatePolicy(vo);
        } else {
            // 없으면 INSERT → crea_by만 사용
            vo.setCreaBy(empId);
            secPolicyMapper.insertPolicy(vo);
        }

        // 저장 후 최종값 다시 조회해서 리턴
        return secPolicyMapper.selectByVendId(vendId);
    }

    // 요청값 → 저장용 VO 복사 로직
    private void applyFromRequest(SecPolicyVO src, SecPolicyVO dest) {
        dest.setPwFailCnt(src.getPwFailCnt());
        dest.setAutoUnlockTm(src.getAutoUnlockTm());
        dest.setPwLenMin(src.getPwLenMin());
        dest.setPwLenMax(src.getPwLenMax());

        dest.setUseUpperYn(src.getUseUpperYn());
        dest.setUseLowerYn(src.getUseLowerYn());
        dest.setUseNumYn(src.getUseNumYn());
        dest.setUseSpclYn(src.getUseSpclYn());

        dest.setCaptchaYn(src.getCaptchaYn());
        dest.setCaptchaFailCnt(src.getCaptchaFailCnt());

        dest.setOtpYn(src.getOtpYn());
        dest.setOtpValidMin(src.getOtpValidMin());
        dest.setOtpFailCnt(src.getOtpFailCnt());

        dest.setSessionTimeoutMin(src.getSessionTimeoutMin());
        dest.setTimeoutAction(src.getTimeoutAction());
    }
    
    private void validatePolicy(SecPolicyVO vo) {

        // 1) 로그인 실패 허용 횟수 3~10
        if (vo.getPwFailCnt() == null
                || vo.getPwFailCnt() < 3
                || vo.getPwFailCnt() > 10) {
            throw new IllegalArgumentException("로그인 실패 허용 횟수는 3~10회 사이여야 합니다.");
        }

        // 2) 자동 잠금 해제 시간
        Integer autoUnlockTm = vo.getAutoUnlockTm();
        if (autoUnlockTm == null) {
            autoUnlockTm = 0;
            vo.setAutoUnlockTm(0);
        }
        if (autoUnlockTm < 0 || autoUnlockTm > 120) {
            throw new IllegalArgumentException("잠금 유지 시간은 0~120분 사이여야 합니다.");
        }

        // 3) 비밀번호 길이
        Integer pwLenMin = vo.getPwLenMin();
        Integer pwLenMax = vo.getPwLenMax();

        if (pwLenMin == null || pwLenMax == null) {
            throw new IllegalArgumentException("비밀번호 최소/최대 길이는 필수입니다.");
        }
        if (pwLenMin < 8 || pwLenMin > 30) {
            throw new IllegalArgumentException("비밀번호 최소 길이는 8~30자 사이여야 합니다.");
        }
        if (pwLenMax < 8 || pwLenMax > 30) {
            throw new IllegalArgumentException("비밀번호 최대 길이는 8~30자 사이여야 합니다.");
        }
        if (pwLenMin > pwLenMax) {
            throw new IllegalArgumentException("비밀번호 최소 길이는 최대 길이보다 클 수 없습니다.");
        }

        // 4) 세션 타임아웃
        Integer sessionTimeoutMin = vo.getSessionTimeoutMin();
        if (sessionTimeoutMin == null
                || sessionTimeoutMin < 15
                || sessionTimeoutMin > 120) {
            throw new IllegalArgumentException("세션 타임 아웃은 15~120분 사이여야 합니다.");
        }

        // 5) OTP
        boolean otpOn = Y.equals(vo.getOtpYn());
        if (otpOn) {
            Integer otpValidMin = vo.getOtpValidMin();
            Integer otpFailCnt  = vo.getOtpFailCnt();

            if (otpValidMin == null
                    || otpValidMin < 1
                    || otpValidMin > 30) {
                throw new IllegalArgumentException("OTP 유효 시간은 1~30분 사이여야 합니다.");
            }
            if (otpFailCnt == null
                    || otpFailCnt < 1
                    || otpFailCnt > 10) {
                throw new IllegalArgumentException("OTP 최대 실패 횟수는 1~10회 사이여야 합니다.");
            }
        }

        // 6) CAPTCHA
        boolean captchaOn = Y.equals(vo.getCaptchaYn());
        if (captchaOn) {
            Integer captchaFailCnt = vo.getCaptchaFailCnt();
            if (captchaFailCnt == null
                    || captchaFailCnt < 0
                    || captchaFailCnt > 5) {
                throw new IllegalArgumentException("CAPTCHA 요구 실패 횟수는 0~5회 사이여야 합니다.");
            }
        }
    }
    
    
}
