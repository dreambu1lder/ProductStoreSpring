package productstore.servlet.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import productstore.servlet.dto.ProductDTO;
import productstore.model.Product;

@Mapper
public interface ProductMapper {

    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    ProductDTO toProductDTO(Product product);

    Product toProduct(ProductDTO productDTO);
}
