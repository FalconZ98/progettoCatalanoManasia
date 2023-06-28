package com.catalanomanasia.project.repository;

import com.catalanomanasia.project.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public interface StoreRepository extends JpaRepository<Store, Integer>{
    List<Store> findByOrderByNameAsc();
    Optional<Store> findByName(String name);
}