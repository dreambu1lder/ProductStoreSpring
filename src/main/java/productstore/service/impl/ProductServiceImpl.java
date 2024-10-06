package productstore.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import productstore.controller.dto.input.ProductInputDTO;
import productstore.controller.dto.output.ProductOutputDTO;
import productstore.controller.mapper.ProductMapper;
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

    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product with id " + id + " not found."));
    }

    public List<ProductOutputDTO> getAllProducts() {
        return productMapper.toDTOs(productRepository.findAll());
    }

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
        productRepository.deleteById(id);
    }
}
