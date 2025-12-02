package store.yd2team.insa.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import store.yd2team.insa.service.HldyVO;

@Mapper
public interface WkTyMapper {

    // 휴일 기준 전체 조회
    List<HldyVO> selectHlDyList();

    // 휴일 기준 신규 등록
    int insertHlDy(HldyVO vo);

    // 휴일 기준 수정
    int updateHlDy(HldyVO vo);

    // 휴일 기준 삭제
    int deleteHlDy(Long hldyNo);
}
