package productstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import productstore.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
