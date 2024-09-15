package productstore.service;

import productstore.servlet.dto.ProductDTO;

import java.sql.SQLException;
import java.util.List;

public interface ProductService {
    ProductDTO createProduct(ProductDTO productDto) throws SQLException;
    ProductDTO getProductById(long id) throws SQLException;
    List<ProductDTO> getAllProducts() throws SQLException;
    List<ProductDTO> getProductsWithPagination(int pageNumber, int pageSize) throws SQLException;
    void updateProduct(ProductDTO productDto) throws SQLException;
    void deleteProduct(long id) throws SQLException;
    ProductDTO getProductWithOrdersById(long id) throws SQLException;
}
