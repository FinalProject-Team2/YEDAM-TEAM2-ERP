// src/main/java/store/yd2team/insa/mapper/SalyLedgMapper.java
package store.yd2team.insa.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import store.yd2team.insa.service.EmpVO;
import store.yd2team.insa.service.SalyLedgVO;
import store.yd2team.insa.service.SalySpecVO;

@Mapper
public interface SalyLedgMapper {

    // 모달 사원 목록
    List<EmpVO> selectEmpListForSaly(
            @Param("vendId") String vendId,
            @Param("deptId") String deptId,
            @Param("empNm")  String empNm
    );

    // 급여대장 목록
    List<SalyLedgVO> selectSalyLedgList(
            @Param("vendId")      String vendId,
            @Param("deptId")      String deptId,
            @Param("salyLedgNm")  String salyLedgNm,
            @Param("payDtStart")  String payDtStart,
            @Param("payDtEnd")    String payDtEnd
    );

    // 급여대장 1건 조회
    SalyLedgVO selectSalyLedgById(@Param("salyLedgId") String salyLedgId);

    int insertSalyLedg(SalyLedgVO vo);
    int updateSalyLedg(SalyLedgVO vo);

    int deleteSalySpecByLedgId(@Param("salyLedgId") String salyLedgId);
    int insertSalySpecList(@Param("list") java.util.List<SalySpecVO> list);
}
