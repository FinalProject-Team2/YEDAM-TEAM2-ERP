package store.yd2team.business.service;

import java.util.Date;

import lombok.Data;

@Data
public class PotentialStdrVO {

    private String stdrDetailId;   // 조건상세 STDR_DETAIL_ID
    private String stdrId;         // 조건 STDR_ID
    private String vendId;         // 회사 VEND_ID (세션 기준)
    private String stdrIteamInfo;  // 조건상세항목 STDR_ITEAM_INFO
    private Integer infoScore;     // 점수 INFO_SCORE

    // 생성/수정 공통 정보
    private Date   creaDt;         // 생성일시
    private String creaBy;         // 생성자 (EMP_ID)
    private Date   updtDt;         // 수정일시
    private String updtBy;         // 수정자 (EMP_ID)

    // 그리드에서 INSERT/UPDATE 구분용
    private String rowStatus;      // "I" = insert, "U" = update
}
