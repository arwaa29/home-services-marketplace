package com.homeservices.userservice.repository;
import com.homeservices.userservice.entity.User;
import com.homeservices.userservice.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long>
{
    Optional<User> findByUsername(String username); //hnst5dmha login


    boolean existsByUsername(String username); //hnst5dmha f register

    List<User> findByRole(Role role);

}
