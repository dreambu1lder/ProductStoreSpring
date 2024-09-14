package productstore.dao;

import productstore.model.Product;

import java.sql.SQLException;
import java.util.List;

public interface ProductDao {

    Product saveProduct(Product product) throws SQLException;
    void deleteProduct(long id) throws SQLException;
    void updateProduct(Product product) throws SQLException;
    Product getProductById(long id) throws SQLException;
    List<Product> getAllProducts() throws SQLException;
    List<Product> getProductWithPagination(int pageNumber, int pageSize) throws SQLException;
    Product getProductWithOrdersById(long id) throws SQLException;
}
