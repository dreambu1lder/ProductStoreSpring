package productstore.service.impl;

import productstore.dao.ProductDao;
import productstore.model.Order;
import productstore.model.Product;
import productstore.service.ProductService;
import productstore.service.apierror.ProductNotFoundException;
import productstore.servlet.dto.ProductDTO;
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
    public ProductDTO createProduct(ProductDTO productDto) throws SQLException {
        Product product = productMapper.toProduct(productDto); // Преобразуем DTO в сущность
        Product savedProduct = productDao.saveProduct(product);
        return productMapper.toProductDTO(savedProduct); // Преобразуем сущность обратно в DTO
    }

    @Override
    public ProductDTO getProductById(long id) throws SQLException {
        Product product = productDao.getProductById(id);
        if (product == null) {
            throw new ProductNotFoundException("Product with ID " + id + " not found.");
        }
        return productMapper.toProductDTO(product); // Преобразуем сущность в DTO
    }

    @Override
    public List<ProductDTO> getAllProducts() throws SQLException {
        List<Product> products = productDao.getAllProducts();
        return products.stream()
                .map(productMapper::toProductDTO) // Используем ProductMapper
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO> getProductsWithPagination(int pageNumber, int pageSize) throws SQLException {
        List<Product> products = productDao.getProductWithPagination(pageNumber, pageSize);
        return products.stream()
                .map(productMapper::toProductDTO) // Используем ProductMapper
                .collect(Collectors.toList());
    }

    @Override
    public void updateProduct(ProductDTO productDto) throws SQLException {
        Product product = productMapper.toProduct(productDto);
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
    public ProductDTO getProductWithOrdersById(long id) throws SQLException {
        Product product = productDao.getProductWithOrdersById(id);
        if (product == null) {
            throw new ProductNotFoundException("Product with ID " + id + " not found.");
        }
        // Преобразуем сущность в DTO, включая идентификаторы заказов
        ProductDTO productDTO = productMapper.toProductDTO(product);
        productDTO.setOrderIds(product.getOrders().stream()
                .map(Order::getId) // Преобразуем список заказов в список их идентификаторов
                .collect(Collectors.toList()));
        return productDTO;
    }
}
