package productstore.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import productstore.controller.dto.input.ProductIdsDTO;
import productstore.controller.dto.input.ProductInputDTO;
import productstore.controller.dto.output.ProductOutputDTO;
import productstore.controller.mapper.ProductMapper;
import productstore.model.Order;
import productstore.model.Product;
import productstore.repository.ProductRepository;
import productstore.service.ProductService;
import productstore.service.exception.ProductNotFoundException;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Transactional(readOnly = true)
    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product with id " + id + " not found."));
    }

    @Transactional(readOnly = true)
    public List<ProductOutputDTO> getAllProducts() {
        return productMapper.toDTOs(productRepository.findAll());
    }

    @Transactional(readOnly = true)
    public ProductOutputDTO getProductById(Long id) {
        return productMapper.toDTO(productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product with id " + id + " not found.")));
    }

    @Transactional
    public ProductOutputDTO saveProduct(ProductInputDTO product) {
        return productMapper.toDTO(productRepository.save(productMapper.toEntity(product)));
    }

    @Transactional
    public void deleteProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id " + id));
        for (Order order : product.getOrders()) {
            order.getOrderProducts().remove(product);
        }
        productRepository.deleteById(id);
    }

    @Transactional
    public ProductOutputDTO updateProductById(Long id, ProductInputDTO productInputDTO) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id " + id));
        product.setName(productInputDTO.getName());
        product.setPrice(productInputDTO.getPrice());
        productRepository.save(product);
        return productMapper.toDTO(product);
    }
}
