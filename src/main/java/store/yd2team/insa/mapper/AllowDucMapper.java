package store.yd2team.insa.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import store.yd2team.insa.service.AllowDucVO;

import java.util.List;

@Mapper
public interface AllowDucMapper {

    // ===== 수당 =====
    List<AllowDucVO> selectAllowList(@Param("vendId") String vendId);

    String selectNewAllowId(@Param("vendId") String vendId);

    void insertAllow(AllowDucVO vo);

    void updateAllow(AllowDucVO vo);

    void deleteAllow(@Param("allowId") String allowId,
                     @Param("vendId") String vendId);

    // ===== 공제 =====
    List<AllowDucVO> selectDucList(@Param("vendId") String vendId);

    String selectNewDucId(@Param("vendId") String vendId);

    void insertDuc(AllowDucVO vo);

    void updateDuc(AllowDucVO vo);

    void deleteDuc(@Param("ducId") String ducId,
                   @Param("vendId") String vendId);
}
