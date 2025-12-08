// src/main/java/store/yd2team/insa/service/AllowDucServiceImpl.java
package store.yd2team.insa.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import store.yd2team.insa.mapper.AllowDucMapper;
import store.yd2team.insa.service.AllowDucService;
import store.yd2team.insa.service.AllowDucVO;

@Service
@RequiredArgsConstructor
public class AllowDucServiceImpl implements AllowDucService {

    private final AllowDucMapper mapper;

    @Override
    public List<AllowDucVO> selectAllowList(String vendId) {
        return mapper.selectAllowList(vendId);
    }

    @Override
    public List<AllowDucVO> selectDucList(String vendId) {
        return mapper.selectDucList(vendId);
    }

    @Override
    @Transactional
    public void saveAllowItems(List<AllowDucVO> created,
                               List<AllowDucVO> updated,
                               List<AllowDucVO> deleted,
                               String vendId,
                               String userId) {

        // 생성
        if (created != null) {
            for (AllowDucVO vo : created) {
                if (isEmpty(vo.getAllowNm())) continue;

                String newId = mapper.selectNewAllowId(vendId);
                vo.setAllowId(newId);
                vo.setVendId(vendId);
                vo.setYnCode(defaultYn(vo.getYnCode()));
                vo.setCreaBy(userId);

                mapper.insertAllow(vo);
            }
        }

        // 수정
        if (updated != null) {
            for (AllowDucVO vo : updated) {
                if (isEmpty(vo.getAllowId())) continue;

                vo.setVendId(vendId);
                vo.setYnCode(defaultYn(vo.getYnCode()));
                vo.setUpdtBy(userId);

                mapper.updateAllow(vo);
            }
        }

        // 삭제
        if (deleted != null) {
            for (AllowDucVO vo : deleted) {
                if (isEmpty(vo.getAllowId())) continue;
                mapper.deleteAllow(vo.getAllowId(), vendId);
            }
        }
    }

    @Override
    @Transactional
    public void saveDucItems(List<AllowDucVO> created,
                             List<AllowDucVO> updated,
                             List<AllowDucVO> deleted,
                             String vendId,
                             String userId) {

        // 생성
        if (created != null) {
            for (AllowDucVO vo : created) {
                if (isEmpty(vo.getDucNm())) continue;

                String newId = mapper.selectNewDucId(vendId);
                vo.setDucId(newId);
                vo.setVendId(vendId);
                vo.setYnCode(defaultYn(vo.getYnCode()));
                vo.setCreaBy(userId);

                mapper.insertDuc(vo);
            }
        }

        // 수정
        if (updated != null) {
            for (AllowDucVO vo : updated) {
                if (isEmpty(vo.getDucId())) continue;

                vo.setVendId(vendId);
                vo.setYnCode(defaultYn(vo.getYnCode()));
                vo.setUpdtBy(userId);

                mapper.updateDuc(vo);
            }
        }

        // 삭제
        if (deleted != null) {
            for (AllowDucVO vo : deleted) {
                if (isEmpty(vo.getDucId())) continue;
                mapper.deleteDuc(vo.getDucId(), vendId);
            }
        }
    }

    private boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    private String defaultYn(String yn) {
        if (isEmpty(yn)) return "e1";
        return yn;
    }
}
