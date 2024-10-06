package productstore.service;

import productstore.controller.dto.input.OrderInputDTO;
import productstore.controller.dto.input.ProductIdsDTO;
import productstore.controller.dto.output.OrderOutputDTO;
import productstore.model.Order;
import productstore.model.Product;

import java.util.List;
import java.util.Optional;

public interface OrderService {

    List<OrderOutputDTO> getAllOrders();

    OrderOutputDTO getOrderById(Long id);

    OrderOutputDTO saveOrder(OrderInputDTO orderInputDTO);

    void deleteOrderById(Long id);

    OrderOutputDTO updateOrderById(Long id, ProductIdsDTO productIdsDTO);
}
