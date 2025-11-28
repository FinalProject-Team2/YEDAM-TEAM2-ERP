package store.yd2team.business.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import store.yd2team.business.mapper.CustcomMapper;
import store.yd2team.business.service.CustcomService;
import store.yd2team.business.service.CustcomVO;

@Service
@RequiredArgsConstructor
public class CustcomServiceImpl implements CustcomService {

    private final CustcomMapper custcomMapper;

    @Override
    public List<CustcomVO> searchCustcom(CustcomVO vo) {
        return custcomMapper.searchCustcom(vo);
    }
}
