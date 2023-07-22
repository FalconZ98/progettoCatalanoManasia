package com.catalanomanasia.project.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
/* Queste annotazioni provengono dal framework Project Lombok e vengono utilizzate per generare automaticamente i metodi getter e setter
per i campi della classe. Questo semplifica il codice, poiché non è necessario scrivere manualmente i metodi getter e setter per ogni campo.
*/

@NoArgsConstructor
/*Questa annotazione crea automaticamente un costruttore senza argomenti per la classe. Un costruttore senza argomenti è spesso richiesto da alcuni framework
o librerie, come ad esempio JPA (Java Persistence API), che possono istanziare oggetti usando il costruttore di default.*/

@AllArgsConstructor
/*Questa annotazione crea un costruttore con tutti gli argomenti per la classe.
Questo costruttore sarà in grado di inizializzare tutti i campi della classe contemporaneamente.*/

@Entity
/* L'annotazione @Entity in Java Spring viene utilizzata per specificare che una classe è un'entità.
 Questa annotazione viene utilizzata nel contesto di Java Persistence API (JPA) per mappare una classe a una tabella nel database*/

@Table(name="roles")
/*Questa annotazione specifica il nome della tabella nel database che sarà associata a questa classe.
Nel caso specifico, la tabella nel database si chiamerà "roles".*/

public class Role
{
    @Id
    /*Questa annotazione indica che il campo seguito è la chiave primaria della tabella nel database.*/

    @Column(name = "id")
    /*Questa annotazione specifica il nome della colonna nel database corrispondente al campo id.*/

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    /* Questa annotazione specifica come viene generato il valore del campo id.
    In questo caso, viene utilizzata la strategia "IDENTITY", che indica che il valore verrà generato automaticamente dal database
    (ad esempio, utilizzando un auto-incremento nel caso di database come MySQL o PostgreSQL).*/

    private Integer id;

    @Column(nullable=false, unique=true)
    /*Questa annotazione specifica che il campo name deve essere non nullo (nullable=false) e deve essere unico (unique=true)
    all'interno della tabella nel database.*/
    private String name;

    @OneToMany(mappedBy="role")
    /* Questa annotazione specifica una relazione uno-a-molti tra la classe Role e la classe User.
    Indica che ogni istanza di Role può avere molteplici istanze di User, e la relazione è mappata dal campo role nella classe User.*/
    private List<User> users;
}
