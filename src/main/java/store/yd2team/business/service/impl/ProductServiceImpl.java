package store.yd2team.business.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import store.yd2team.business.mapper.ProductMapper;
import store.yd2team.business.service.ProductService;
import store.yd2team.business.service.ProductVO;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;

    @Override
    public List<ProductVO> searchProduct(ProductVO vo) {
        return productMapper.searchProduct(vo);
    }
}
