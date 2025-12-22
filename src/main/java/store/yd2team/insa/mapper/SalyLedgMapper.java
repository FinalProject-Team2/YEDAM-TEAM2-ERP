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

    // ================= 모달: 사원 목록 =================
    List<EmpVO> selectEmpListForSaly(@Param("vendId") String vendId,
                                     @Param("deptId") String deptId,
                                     @Param("empNm") String empNm);

    // ================= ID 채번 =================
    /** 급여대장 ID: SL_YYYYMMDD_0001 */
    String selectNewSalyLedgId();

    /** 명세서 ID: SP_YYYYMMDD_0001 */
    String selectNewSalySpecId();

    // ================= 급여대장 목록/단건 =================
    List<SalyLedgVO> selectSalyLedgList(@Param("vendId") String vendId,
                                        @Param("salyLedgNm") String salyLedgNm,
                                        @Param("payDtStart") String payDtStart,
                                        @Param("payDtEnd") String payDtEnd);

    SalyLedgVO selectSalyLedgById(@Param("salyLedgId") String salyLedgId);

    // 해당 급여대장에 포함된 사원 ID 목록
    List<String> selectEmpIdListByLedgId(@Param("salyLedgId") String salyLedgId);

    // ================= 급여대장 저장/삭제 =================
    int insertSalyLedg(SalyLedgVO vo);

    int updateSalyLedg(SalyLedgVO vo);

    int deleteSalyLedg(@Param("salyLedgId") String salyLedgId);
    
    int deleteSalySpecItemByLedgId(@Param("salyLedgId") String salyLedgId);


    // ================= 명세서 =================
    int deleteSalySpecByLedgId(@Param("salyLedgId") String salyLedgId);

    int insertSalySpecList(@Param("list") List<SalySpecVO> list);
    
    int countUncalculatedSpec(@Param("salyLedgId") String salyLedgId,
            @Param("vendId") String vendId);

int updateSalyLedgStatus(@Param("salyLedgId") String salyLedgId,
           @Param("vendId") String vendId,
           @Param("salyLedgSt") String salyLedgSt,
           @Param("updtBy") String updtBy);

}
