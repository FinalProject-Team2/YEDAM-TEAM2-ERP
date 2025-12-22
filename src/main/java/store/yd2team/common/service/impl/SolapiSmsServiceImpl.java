package store.yd2team.common.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.solapi.sdk.SolapiClient;
import com.solapi.sdk.message.model.Message;
import com.solapi.sdk.message.service.DefaultMessageService;

import lombok.extern.slf4j.Slf4j;
import store.yd2team.common.service.SmsService;

@Slf4j
@Service
public class SolapiSmsServiceImpl implements SmsService {

	private final DefaultMessageService messageService;

    @Value("${solapi.from-number}")
    private String fromNumber;

    public SolapiSmsServiceImpl(
            @Value("${solapi.api-key}") String apiKey,
            @Value("${solapi.api-secret}") String apiSecret
    ) {
        // SOLAPI 클라이언트 초기화
        this.messageService = SolapiClient.INSTANCE.createInstance(apiKey, apiSecret);
    }

    @Override
    public void sendOtpSms(String to, String otpCode, int validMin) {

        // 번호에서 숫자만 남기기 (SOLAPI는 '-' 허용 안 됨)
        String cleanTo   = to.replaceAll("[^0-9]", "");
        String cleanFrom = fromNumber.replaceAll("[^0-9]", "");

        Message message = new Message();
        message.setFrom(cleanFrom);
        message.setTo(cleanTo);
        message.setText("[ERP] OTP 인증번호 [" + otpCode + "] 를 입력해 주세요.\n(유효시간 "
                        + validMin + "분)");
        
        log.info("[SMS-DRY-RUN][OTP] to={}, text={}", cleanTo, message);
		/* 정재민 나중에 주석해제 할 거임 */  // 실제 문자 발송
		  try { messageService.send(message); 
		  log.info("SOLAPI OTP 전송 성공: to={}", cleanTo); } catch (Exception e) { 
		  log.error("SOLAPI OTP 전송 실패: to={}, err={}", cleanTo, e.getMessage(), e); }
		 
    }

    @Override
    public void sendTempPasswordSms(String to,String vendId, String loginId, String tempPassword) {

        String cleanTo   = to.replaceAll("[^0-9]", "");
        String cleanFrom = fromNumber.replaceAll("[^0-9]", "");

        Message message = new Message();
        message.setFrom(cleanFrom);
        message.setTo(cleanTo);
        message.setText(
                "[ERP] 임시 비밀번호 안내\n" +
                "회사코드 : " + vendId + "\n" + 
                "ID : " + loginId + "\n" +
                "임시 비밀번호 : " + tempPassword + "\n" +
                "로그인 후 반드시 비밀번호를 변경해 주세요."
        );

        log.info("[SMS-DRY-RUN][OTP] to={}, text={}", cleanTo, message);
		  /* 정재민 나중에 주석 해제할 거임 */
		  try { messageService.send(message); 
		  log.info("SOLAPI 임시 비밀번호 전송 성공: to={}", cleanTo); } catch (Exception e) {
		  log.error("SOLAPI 임시 비밀번호 전송 실패: to={}, err={}", cleanTo, e.getMessage(), e);
		  }
		 
    }
	
}
