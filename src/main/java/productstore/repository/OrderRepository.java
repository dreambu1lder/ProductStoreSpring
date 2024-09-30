package productstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import productstore.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}
