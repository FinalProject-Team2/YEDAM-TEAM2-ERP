package store.yd2team.common.web;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import store.yd2team.common.consts.SessionConst;
import store.yd2team.common.dto.SessionDto;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ProfileImageController {

    @GetMapping("/emp/profile/photo")
    public ResponseEntity<Resource> profilePhoto(HttpSession session) throws IOException {

        SessionDto loginEmp = (SessionDto) session.getAttribute(SessionConst.LOGIN_EMP);

        // 1) 세션 없거나, 사진 경로 없으면 → 기본 이미지
        if (loginEmp == null || loginEmp.getProofPhoto() == null || loginEmp.getProofPhoto().isBlank()) {
            log.debug("세션 또는 proofPhoto 없음 → 기본 이미지 반환");
            return defaultProfileImage();
        }

        String proofPhoto = loginEmp.getProofPhoto();   // 예: /images/profil/emp2512001.jpg
        log.debug("세션 proofPhoto = {}", proofPhoto);

        // 2) 실제 파일 경로: {user.dir}/upload + /images/profil/emp2512001.jpg
        Path filePath = Paths.get(
                System.getProperty("user.dir"),
                "upload",
                proofPhoto.replaceFirst("^/", "")    // 맨 앞의 "/" 제거 → images/profil/emp2512001.jpg
        );
        log.debug("실제 파일 경로 = {}", filePath);

        if (!Files.exists(filePath)) {
            log.warn("프로필 이미지 파일이 존재하지 않음: {}", filePath);
            return defaultProfileImage();
        }

        // MIME 타입 추론 (jpg/png 등)
        String contentType = Files.probeContentType(filePath);
        MediaType mediaType = (contentType != null)
                ? MediaType.parseMediaType(contentType)
                : MediaType.IMAGE_JPEG;

        Resource fileResource = new org.springframework.core.io.PathResource(filePath);

        return ResponseEntity
                .ok()
                .contentType(mediaType)
                .body(fileResource);
    }

    // 3) 기본 이미지: classpath:/static/assets/images/faces/face8.jpg 사용
    private ResponseEntity<Resource> defaultProfileImage() throws IOException {
        ClassPathResource imgFile = new ClassPathResource("static/assets/images/faces/face8.jpg");
        log.debug("기본 이미지 존재 여부 = {}", imgFile.exists());

        if (!imgFile.exists()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity
                .ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(imgFile);
    }
}
