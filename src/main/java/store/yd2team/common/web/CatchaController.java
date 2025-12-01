package store.yd2team.common.web;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import store.yd2team.common.consts.SessionConst;
import store.yd2team.common.service.CaptchaService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CatchaController {

	private final CaptchaService captchaService;

	/**
	 * 로그인 화면에서 캡챠 이미지 요청 <img src="/logIn/captcha"> 로 사용
	 */
	@GetMapping(value = "/logIn/captcha", produces = MediaType.IMAGE_PNG_VALUE)
	public @ResponseBody byte[] getCaptcha(HttpSession session) throws IOException {

		// 1) 랜덤 문자열 생성 (길이 5)
		String text = captchaService.generateText(5);

		// 2) 세션에 정답 저장
		session.setAttribute(SessionConst.LOGIN_CAPTCHA_ANSWER, text);
		log.debug("생성된 캡챠: {}", text); // 개발용, 나중에 필요 없으면 삭제

		// 3) 이미지 생성
		BufferedImage image = captchaService.createImage(text);

		// 4) 이미지 → byte[] 로 변환해서 반환
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(image, "png", baos);

		return baos.toByteArray();
	}

}
