package com.catalanomanasia.project.repository;

import com.catalanomanasia.project.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface TransactionRepository extends JpaRepository<Transaction, Integer>{
    List<Transaction> findAllByOrderByTimeDesc();
    @Query("Select t from Transaction t INNER JOIN t.creditCard c INNER JOIN c.store s WHERE s.id = ?1")
    List<Transaction> findAllByStore(Integer storeId);
    @Query("Select t from Transaction t INNER JOIN t.creditCard c INNER JOIN c.owner u WHERE u.id = ?1")
    List<Transaction> findAllByUser(Integer userId);

}