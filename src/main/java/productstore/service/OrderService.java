package productstore.service;

import productstore.controller.dto.input.OrderInputDTO;
import productstore.controller.dto.input.ProductIdsDTO;
import productstore.controller.dto.output.OrderOutputDTO;

import java.util.List;

public interface OrderService {

    List<OrderOutputDTO> getAllOrders();

    List<OrderOutputDTO> getAllOrdersWithProducts();

    OrderOutputDTO getOrderById(Long id);

    OrderOutputDTO saveOrder(OrderInputDTO orderInputDTO);

    void deleteOrderById(Long id);

    OrderOutputDTO updateOrderById(Long id, ProductIdsDTO productIdsDTO);
}
