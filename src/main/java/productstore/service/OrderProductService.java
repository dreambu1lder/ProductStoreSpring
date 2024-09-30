package productstore.service;

import productstore.model.OrderProduct;
import productstore.model.OrderProductPK;

import java.util.List;
import java.util.Optional;

public interface OrderProductService {

    List<OrderProduct> getAllOrderProducts();

    Optional<OrderProduct> getOrderProductById(OrderProductPK id);

    OrderProduct saveOrderProduct(OrderProduct orderProduct);

    void deleteOrderProduct(OrderProductPK id);
}
