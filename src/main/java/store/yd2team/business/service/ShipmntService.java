package store.yd2team.business.service;

import java.util.List;

public interface ShipmntService {

    // 출하 조회
    List<ShipmntVO> selectShipmntList(ShipmntVO vo);

    // 출하완료 처리 (다건)
    void completeShipment(List<String> oustIds);
}

