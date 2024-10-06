package productstore.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import productstore.controller.dto.input.ProductInputDTO;
import productstore.controller.dto.output.ProductOutputDTO;
import productstore.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<ProductOutputDTO>> getAllProducts() {
        return new ResponseEntity<>(productService.getAllProducts(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ProductOutputDTO> saveProduct(@Valid @RequestBody ProductInputDTO productInputDTO) {
        return new ResponseEntity<>(productService.saveProduct(productInputDTO), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductOutputDTO> getProductById(@PathVariable("id") Long id) {
        return new ResponseEntity<>(productService.getProductById(id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductById(@PathVariable("id") Long id) {
        productService.deleteProductById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductOutputDTO> updateProductById(@PathVariable("id") Long id, @Valid @RequestBody ProductInputDTO productInputDTO) {
        return new ResponseEntity<>(productService.updateProductById(id, productInputDTO), HttpStatus.ACCEPTED);
    }

}
