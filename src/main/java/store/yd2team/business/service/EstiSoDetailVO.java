package store.yd2team.business.service;

import lombok.Data;

@Data
public class EstiSoDetailVO {

	// tb_esti_detail
    private Long estiDetailNo;   // esti_detail_no
    private String estiId;         // esti_id
    private String version;     // version
    private String vendId;       // vend_id
    private String productId;      // product_id
    private Long qy;          // qy
    private Long price;          // price
    private Long supplyAmt;      // supply_amt
    private String rm;           // rm
    
    // 주문 상세(tb_so_detail)
    private Long soDetailNo;
    private String soId;
    private Long supplyPrice;
    private Long surtaxPrice;

    // 화면용
    private String productName;

    // 공통
    private String creaBy;
    private String updtBy;
}

