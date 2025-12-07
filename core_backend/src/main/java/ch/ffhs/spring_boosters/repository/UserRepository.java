package ch.ffhs.spring_boosters.repository;

import ch.ffhs.spring_boosters.controller.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByUsername(String username);

    Optional<User> findById(UUID id);

    boolean existsByUsername(String username);

    @Query("SELECT u FROM User u WHERE u.role = ?1")
    Optional<List<User>> findAllByRole(String role);
}
