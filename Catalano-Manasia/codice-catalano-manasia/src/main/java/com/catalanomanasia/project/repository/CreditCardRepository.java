package com.catalanomanasia.project.repository;

import com.catalanomanasia.project.model.CreditCard;
import com.catalanomanasia.project.model.Store;
import com.catalanomanasia.project.model.User;
import jakarta.annotation.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
/*Questa annotazione viene utilizzata per dichiarare una classe come componente gestito dal contesto di Spring,
che sarà quindi gestita e configurata automaticamente da Spring durante il ciclo di vita dell'applicazione.
Quando una classe è annotata con @Component, Spring la riconosce come un candidato per essere istanziata come bean all'interno del contesto dell'applicazione.*/

public interface CreditCardRepository extends JpaRepository<CreditCard, Integer> {
    // Questo metodo cerca una carta di credito in base al numero della carta.
    // L'annotazione '@Nullable' indica che il risultato può essere null se non viene trovata alcuna corrispondenza.
    @Nullable
    CreditCard findCreditCardByNumber(String number);

    // Questo metodo cerca l'ultima carta di credito inserita, ordinando le carte per ID in ordine discendente.
    // 'findTopByOrderByIdDesc()' restituisce solo la prima corrispondenza.
    CreditCard findTopByOrderByIdDesc();

    // Questo metodo cerca tutte le carte di credito senza proprietario.
    List<CreditCard> findCreditCardsByOwnerNull();

    // Questo metodo cerca tutte le carte di credito senza proprietario e appartenenti a uno specifico negozio, filtrate per lo stato di abilitazione.
    List<CreditCard> findCreditCardsByOwnerNullAndStoreAndEnabled(Store store, Integer enabled);

    // Questo metodo cerca tutte le carte di credito appartenenti a uno specifico utente proprietario.
    List<CreditCard> findCreditCardsByOwner(User owner);
}
