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
import java.util.stream.Collectors;

public class ProductServiceImpl implements ProductService {
    private final ProductDao productDao;
    private final ProductMapper productMapper = ProductMapper.INSTANCE;

    public ProductServiceImpl(ProductDao productDao) {
        this.productDao = productDao;
    }

    @Override
    public ProductOutputDTO createProduct(ProductInputDTO productInputDTO) throws SQLException {
        Product product = productMapper.toProduct(productInputDTO);
        Product savedProduct = productDao.saveProduct(product);
        return productMapper.toProductOutputDTO(true, savedProduct); // Указываем true для включения orderIds
    }

    @Override
    public ProductOutputDTO getProductById(long id) throws SQLException {
        Product product = productDao.getProductById(id);
        if (product == null) {
            throw new ProductNotFoundException("Product with ID " + id + " not found.");
        }
        return productMapper.toProductOutputDTO(true, product); // Указываем true для включения orderIds
    }

    @Override
    public List<ProductOutputDTO> getAllProducts() throws SQLException {
        List<Product> products = productDao.getAllProducts();
        return products.stream()
                .map(product -> productMapper.toProductOutputDTO(true, product)) // Указываем true для включения orderIds
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductOutputDTO> getProductsWithPagination(int pageNumber, int pageSize) throws SQLException {
        List<Product> products = productDao.getProductWithPagination(pageNumber, pageSize);
        return products.stream()
                .map(product -> productMapper.toProductOutputDTO(true, product)) // Указываем true для включения orderIds
                .collect(Collectors.toList());
    }

    @Override
    public void updateProduct(ProductInputDTO productInputDTO) throws SQLException {
        Product product = productMapper.toProduct(productInputDTO);
        if (productDao.getProductById(product.getId()) == null) {
            throw new ProductNotFoundException("Product with ID " + product.getId() + " not found.");
        }
        productDao.updateProduct(product);
    }

    @Override
    public void deleteProduct(long id) throws SQLException {
        if (productDao.getProductById(id) == null) {
            throw new ProductNotFoundException("Product with ID " + id + " not found.");
        }
        productDao.deleteProduct(id);
    }

    @Override
    public ProductOutputDTO getProductWithOrdersById(long id) throws SQLException {
        Product product = productDao.getProductWithOrdersById(id);
        if (product == null) {
            throw new ProductNotFoundException("Product with ID " + id + " not found.");
        }
        return productMapper.toProductOutputDTO(true, product); // Указываем true для включения orderIds
    }
}
