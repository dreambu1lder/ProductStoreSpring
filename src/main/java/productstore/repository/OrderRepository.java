package productstore.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import productstore.model.Order;
import productstore.model.Product;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = {"user", "orderProducts"})
    List<Order> findAll();

    @EntityGraph(attributePaths = {"user", "orderProducts"})
    Optional<Order> findById(Long id);

    List<Order> findAllByOrderProductsContaining(Product product);
}
