package productstore.service;

import productstore.servlet.dto.input.ProductInputDTO;
import productstore.servlet.dto.output.ProductOutputDTO;

import java.sql.SQLException;
import java.util.List;

public interface ProductService {
    ProductOutputDTO createProduct(ProductInputDTO productInputDTO) throws SQLException;

    ProductOutputDTO getProductById(long id) throws SQLException;

    List<ProductOutputDTO> getAllProducts() throws SQLException;

    List<ProductOutputDTO> getProductsWithPagination(int pageNumber, int pageSize) throws SQLException;

    void updateProduct(ProductInputDTO productInputDTO) throws SQLException;

    void deleteProduct(long id) throws SQLException;

    ProductOutputDTO getProductWithOrdersById(long id) throws SQLException;
}
