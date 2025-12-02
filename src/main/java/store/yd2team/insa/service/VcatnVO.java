package store.yd2team.insa.service;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class VcatnVO {

	private String vcatnId;      // 주식별자
	private String empId;        // 사원_ID(FK)-휴가신청사원
	private String cfmNm;        // 승인자이름
	private String nm;        // 신청자이름
	private String confmerId;    // 사원_ID(FK)-휴가승인관리자
	private String vcatnTy;      // 연차/병가/특별휴가
	private String salyPymntSt;  // 유급/무급
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date vcatnBegin;     // 휴가시작한 날
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date vcatnEnd;       // 휴가종료된 날
	private String vcatnResn;    // 휴가사유적는곳
	private String st;           // 신청/승인/반려
	private String returnResn;   // 반려사유적는곳
	private Double vcatnDe;      // 휴가일수(예 1일=1, 반차=0.5)
	private String rm;           // 비고
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date creaDt;         // 생성날짜
	private String creaBy;       // 생성자
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date updtDt;         // 수정날짜
	private String updtBy;       // 수정자
}
