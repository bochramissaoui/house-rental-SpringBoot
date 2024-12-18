package com.example.location.Repository;

import com.example.location.Entity.PropertyEntity;
import com.example.location.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<PropertyEntity, Long> {
    List<PropertyEntity> findByOwner(UserEntity owner);
}
