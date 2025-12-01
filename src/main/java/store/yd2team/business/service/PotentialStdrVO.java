package store.yd2team.business.service;

import lombok.Data;

@Data
public class PotentialStdrVO {
	private String stdrDetailId;     // 조건상세 STDR_DETAIL_ID
    private String stdrId;           // 조건 	STDR_ID
    private String vendId;           // 거래처 VEND_ID
    private String stdrIteamInfo;    // 조건상세항목 STDR_ITEAM_INFO
    private Integer infoScore;       // 점수 INFO_SCORE
}
