package productstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import productstore.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
