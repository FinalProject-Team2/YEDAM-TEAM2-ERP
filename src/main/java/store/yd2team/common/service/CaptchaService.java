package store.yd2team.common.service;

import java.awt.image.BufferedImage;

public interface CaptchaService {
	
     // length 길이의 랜덤 캡챠 문자열 생성
    String generateText(int length);

    // 주어진 문자열을 그린 캡챠 이미지를 생성
    BufferedImage createImage(String text);
	
}
