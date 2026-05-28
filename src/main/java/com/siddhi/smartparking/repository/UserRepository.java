package com.siddhi.smartparking.repository;
import java.util.Optional;
import com.siddhi.smartparking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
}