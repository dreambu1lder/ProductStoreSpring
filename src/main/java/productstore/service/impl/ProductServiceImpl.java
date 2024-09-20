package productstore.service.impl;

import productstore.dao.ProductDao;
import productstore.model.Product;
import productstore.service.ProductService;
import productstore.service.apierror.ProductNotFoundException;
import productstore.servlet.dto.input.ProductInputDTO;
import productstore.servlet.dto.output.ProductOutputDTO;
import productstore.servlet.mapper.ProductMapper;

import java.sql.SQLException;
import java.util.List;

public class ProductServiceImpl implements ProductService {

    private static final String PRODUCT_WITH_ID = "Product with ID ";
    private static final String NOT_FOUND = " not found.";
    private final ProductDao productDao;
    private final ProductMapper productMapper;

    public ProductServiceImpl(ProductDao productDao, ProductMapper productMapper) {
        this.productDao = productDao;
        this.productMapper = productMapper;
    }

    @Override
    public ProductOutputDTO createProduct(ProductInputDTO productInputDTO) throws SQLException {
        Product product = productMapper.toProduct(productInputDTO);
        Product savedProduct = productDao.saveProduct(product);
        return productMapper.toProductOutputDTO(true, savedProduct);
    }

    @Override
    public ProductOutputDTO getProductById(long id) throws SQLException {
        Product product = productDao.getProductById(id);
        if (product == null) {
            throw new ProductNotFoundException(PRODUCT_WITH_ID + id + NOT_FOUND);
        }
        return productMapper.toProductOutputDTO(true, product);
    }

    @Override
    public List<ProductOutputDTO> getAllProducts() throws SQLException {
        List<Product> products = productDao.getAllProducts();
        return products.stream()
                .map(product -> productMapper.toProductOutputDTO(true, product))
                .toList();
    }

    @Override
    public List<ProductOutputDTO> getProductsWithPagination(int pageNumber, int pageSize) throws SQLException {
        List<Product> products = productDao.getProductWithPagination(pageNumber, pageSize);
        return products.stream()
                .map(product -> productMapper.toProductOutputDTO(true, product))
                .toList();
    }

    @Override
    public void updateProduct(ProductInputDTO productInputDTO) throws SQLException {
        Product product = productMapper.toProduct(productInputDTO);
        if (productDao.getProductById(product.getId()) == null) {
            throw new ProductNotFoundException(PRODUCT_WITH_ID + product.getId() + NOT_FOUND);
        }
        productDao.updateProduct(product);
    }

    @Override
    public void deleteProduct(long id) throws SQLException {
        if (productDao.getProductById(id) == null) {
            throw new ProductNotFoundException(PRODUCT_WITH_ID + id + NOT_FOUND);
        }
        productDao.deleteProduct(id);
    }

    @Override
    public ProductOutputDTO getProductWithOrdersById(long id) throws SQLException {
        Product product = productDao.getProductWithOrdersById(id);
        if (product == null) {
            throw new ProductNotFoundException(PRODUCT_WITH_ID + id + NOT_FOUND);
        }
        return productMapper.toProductOutputDTO(true, product);
    }
}
