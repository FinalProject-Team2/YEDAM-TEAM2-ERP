package store.yd2team.insa.service;

import java.util.List;

public interface WkTyService {

    // 조회
    List<HldyVO> getHlDyList();

    // 단건 등록
    int insertHlDy(HldyVO vo);

    // 단건 수정
    int updateHlDy(HldyVO vo);

    // 단건 삭제
    int deleteHlDy(Long hldyNo);
}
