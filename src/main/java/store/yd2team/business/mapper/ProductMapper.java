package store.yd2team.business.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import store.yd2team.business.service.ProductVO;

@Mapper
public interface ProductMapper {

	 List<ProductVO> searchProduct(ProductVO searchVO);
}
