package com.id.provider.models;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface UserRepository extends JpaRepository<User,Integer> {

    Optional<User> findByEmail(String email);

    @Transactional
    @Modifying
    @Query(value = "UPDATE dsuser SET password = :password WHERE email = :email", nativeQuery = true)
    void changeUserPassword(@Param("email") String email, @Param("password") String password);


}