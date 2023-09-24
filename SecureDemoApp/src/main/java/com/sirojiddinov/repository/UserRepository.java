package com.sirojiddinov.repository;

import com.sirojiddinov.user.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserModel, Integer> {

    Optional<UserModel> findByEmail(String email);
}
