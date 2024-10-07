package productstore.service;

import productstore.controller.dto.input.ProductInputDTO;
import productstore.controller.dto.output.ProductOutputDTO;
import productstore.model.Product;

import java.util.List;

public interface ProductService {

    List<ProductOutputDTO> getAllProducts();

    ProductOutputDTO getProductById(Long id);

    ProductOutputDTO saveProduct(ProductInputDTO product);

    void deleteProductById(Long id);

    Product findById(Long id);

    ProductOutputDTO updateProductById(Long id, ProductInputDTO productInputDTO);
}
