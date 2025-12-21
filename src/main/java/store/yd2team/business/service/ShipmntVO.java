package store.yd2team.business.service;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class ShipmntVO {

	// 조회
	private String oustId;
	private String custcomId;
	private String custcomName;
	private String psch;
	private String pschTel;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate oustRequestDt;
	private String productId;
	private String productName;
	private Long ttPrice;
	private String empId; // 작업자 사원ID
	private String nm; // 작업자 이름
	private String cttpc; // 작업자연락처
	private String shipmntSt; // 출하상태
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate shipmntDt;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate dueStart;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate dueEnd;
	
	
	// 공통
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate creaDt;
	private String creaBy;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate updtDt;
	private String updtBy;
	
	// 출하취소
	private String cancelReason;
	private Integer shipQty;
}
