package store.yd2team.insa.service;

import java.util.List;

public interface AllowDucService {

    // 목록
    List<AllowDucVO> selectAllowList(String vendId);
    List<AllowDucVO> selectDucList(String vendId);

    // 저장
    void saveAllowItems(List<AllowDucVO> created,
                        List<AllowDucVO> updated,
                        List<AllowDucVO> deleted,
                        String vendId,
                        String userId);

    void saveDucItems(List<AllowDucVO> created,
                      List<AllowDucVO> updated,
                      List<AllowDucVO> deleted,
                      String vendId,
                      String userId);
}
