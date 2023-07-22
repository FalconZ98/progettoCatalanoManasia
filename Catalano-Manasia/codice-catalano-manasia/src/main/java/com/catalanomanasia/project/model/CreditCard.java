package com.catalanomanasia.project.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.Objects;
@Entity
@Table(name = "credit_card")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreditCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, name = "number", nullable = false)
    private String number;

    @Column(name = "balance", nullable = false)
    private Integer balance;

    @Column(name = "enabled")
    private Integer enabled;

    @ManyToOne
    // Questo campo indica una relazione molti-a-uno con la classe 'User'
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    // Specifica la colonna del proprietario della chiave esterna nella tabella, mappata alla colonna 'id' della tabella 'User'
    private User owner;

    @ManyToOne
    @JoinColumn(name = "store_id", referencedColumnName = "id")
    private Store store;

}
