package productstore.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import productstore.controller.dto.input.ProductIdsDTO;
import productstore.controller.dto.input.ProductInputDTO;
import productstore.controller.dto.output.ProductOutputDTO;
import productstore.controller.mapper.ProductMapper;
import productstore.model.Order;
import productstore.model.Product;
import productstore.repository.OrderRepository;
import productstore.repository.ProductRepository;
import productstore.service.ProductService;
import productstore.service.exception.ProductNotFoundException;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final OrderRepository orderRepository;

    public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper, OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.orderRepository = orderRepository;
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

        // Удалить продукт из всех заказов
        for (Order order : product.getOrders()) {
            order.getOrderProducts().remove(product);
            orderRepository.save(order); // Сохраняем изменения
        }

        // Очистить список заказов у продукта, чтобы избежать ошибок при попытке удалить продукт
        product.getOrders().clear();

        // Теперь можно удалить продукт
        productRepository.delete(product);
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
