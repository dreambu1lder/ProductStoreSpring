package productstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import productstore.model.OrderProduct;
import productstore.model.OrderProductPK;

public interface OrderProductRepository extends JpaRepository<OrderProduct, OrderProductPK> {
}
