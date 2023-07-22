package com.catalanomanasia.project.repository;

import com.catalanomanasia.project.model.CreditCard;
import com.catalanomanasia.project.model.Store;
import com.catalanomanasia.project.model.User;
import jakarta.annotation.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface CreditCardRepository extends JpaRepository<CreditCard, Integer>{
    @Nullable
    CreditCard findCreditCardByNumber(String number);
    CreditCard findTopByOrderByIdDesc();
    List<CreditCard> findCreditCardsByOwnerNull();
    List<CreditCard> findCreditCardsByOwnerNullAndStoreAndEnabled(Store store, Integer enabled);
    List<CreditCard> findCreditCardsByOwner(User owner);
}