package productstore.service;

import productstore.controller.dto.input.ProductInputDTO;
import productstore.controller.dto.output.ProductOutputDTO;
import productstore.model.Product;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ProductService {

    List<Product> getAllProducts();

    Optional<Product> getProductById(Long id);

    Product saveProduct(Product product);

    void deleteProductById(Long id);
}
