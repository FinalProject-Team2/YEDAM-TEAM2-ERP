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

        try {
            messageService.send(message);   // 실제 문자 발송
            log.info("SOLAPI OTP 전송 성공: to={}", cleanTo);
        } catch (Exception e) {
            // 여기서 예외를 바로 던지면 로그인 전체가 깨질 수 있으니, 일단 로그만 찍는 패턴 추천
            log.error("SOLAPI OTP 전송 실패: to={}, err={}", cleanTo, e.getMessage(), e);
        }
    }
	
}
