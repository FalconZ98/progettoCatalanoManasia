package com.catalanomanasia.project.repository;

import com.catalanomanasia.project.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    // Questo metodo restituisce tutte le transazioni ordinate per tempo in ordine discendente (dalla più recente alla più vecchia).
    List<Transaction> findAllByOrderByTimeDesc();

    // Questo metodo utilizza una query JPQL personalizzata per restituire tutte le transazioni associate a uno specifico negozio.
    // L'annotazione @Query indica che la query specificata sarà eseguita al posto di una query derivata.
    // La query JPQL seleziona tutte le transazioni (t) in cui la carta di credito (c) è associata a un negozio (s) con ID uguale a ?1 (storeId).
    @Query("SELECT t FROM Transaction t INNER JOIN t.creditCard c INNER JOIN c.store s WHERE s.id = ?1")
    List<Transaction> findAllByStore(Integer storeId);

    // Questo metodo utilizza una query JPQL personalizzata per restituire tutte le transazioni associate a uno specifico utente
    // (proprietario della carta di credito).
    // La query JPQL seleziona tutte le transazioni (t) in cui la carta di credito (c) è associata all'utente (u) con ID uguale a ?1 (userId).
    @Query("SELECT t FROM Transaction t INNER JOIN t.creditCard c INNER JOIN c.owner u WHERE u.id = ?1")
    List<Transaction> findAllByUser(Integer userId);
}
