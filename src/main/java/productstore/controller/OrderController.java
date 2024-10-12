package productstore.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import productstore.controller.dto.input.OrderInputDTO;
import productstore.controller.dto.input.ProductIdsDTO;
import productstore.controller.dto.output.OrderOutputDTO;
import productstore.controller.mapper.OrderMapper;
import productstore.service.OrderService;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public OrderOutputDTO saveOrder(@Valid @RequestBody OrderInputDTO orderInputDTO) {
        return orderService.saveOrder(orderInputDTO);
    }

    @GetMapping
    public List<OrderOutputDTO> getAllOrder() {
        return orderService.getAllOrders();
    }

    @GetMapping("/products")
    public ResponseEntity<List<OrderOutputDTO>> getAllOrdersWithProducts() {
        List<OrderOutputDTO> ordersWithProducts = orderService.getAllOrdersWithProducts();
        return new ResponseEntity<>(ordersWithProducts, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrderById(@PathVariable("id") Long id) {
        orderService.deleteOrderById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderOutputDTO> getOrderById(@PathVariable("id") Long id) {
        return new ResponseEntity<>(orderService.getOrderById(id), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderOutputDTO> updateOrderById(@PathVariable("id") Long id, @Valid @RequestBody ProductIdsDTO productIdsDTO) {
        return new ResponseEntity<>(orderService.updateOrderById(id, productIdsDTO), HttpStatus.CREATED);
    }
}
