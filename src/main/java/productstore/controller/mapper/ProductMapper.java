package productstore.controller.mapper;

import org.mapstruct.*;
import productstore.controller.dto.input.ProductInputDTO;
import productstore.controller.dto.output.ProductOutputDTO;
import productstore.model.Product;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductOutputDTO toDTO(Product product);

    List<ProductOutputDTO> toDTOs(List<Product> products);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orders", ignore = true)
    Product toEntity(ProductInputDTO productInputDTO);
}