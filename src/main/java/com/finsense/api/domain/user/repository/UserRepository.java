package com.finsense.api.domain.user.repository;

import com.finsense.api.domain.user.model.User;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<@NonNull User, @NonNull UUID> {
    boolean existsByEmail(String email);
}
