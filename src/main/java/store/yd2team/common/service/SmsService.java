package store.yd2team.common.service;

public interface SmsService {

	/**
     * OTP 문자 발송용
     * @param to       수신번호 (010숫자만, '-' 없이)
     * @param otpCode  발송할 OTP 코드
     * @param validMin 유효 시간(분) - 안내 메시지에 사용
     */
    void sendOtpSms(String to, String otpCode, int validMin);
    
    void sendTempPasswordSms(String to, String vendId, String loginId, String tempPassword);
	
}
