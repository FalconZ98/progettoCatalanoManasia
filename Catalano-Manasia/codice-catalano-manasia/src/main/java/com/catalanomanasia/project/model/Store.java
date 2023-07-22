package com.catalanomanasia.project.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "store")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Integer id;
    private String name;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH})
    @JoinTable(
            name = "users_registered_stores",
            joinColumns = @JoinColumn(name = "store_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    Set<User> registeredUsers;

    public void addCustomer(User c){
        this.registeredUsers.add(c);
        c.getRegisteredStores().add(this);
    }

    public void removeUser(Integer userId) {
        User user = this.registeredUsers.stream().filter(t -> t.getId() == userId).findFirst().orElse(null);
        if (user != null) {
            this.registeredUsers.remove(user);
            user.getRegisteredStores().remove(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Store store = (Store) o;
        return Objects.equals(id, store.id) && Objects.equals(name, store.name);
    }

    /* Il codice fornito include l'annotazione @Override, che indica che il metodo equals() sta sovrascrivendo un metodo 
    della classe genitore. Il metodo equals() è un metodo speciale di Java che viene utilizzato per confrontare due oggetti e 
    determinare se sono uguali o meno. Il metodo equals() ereditato dalla classe Object di Java confronta gli oggetti in base 
    all'indirizzo di memoria, il che significa che due oggetti con gli stessi valori ma in posizioni di memoria diverse 
    sarebbero considerati diversi. */


    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    /*Nel codice fornito, il metodo hashCode() viene sovrascritto per calcolare il codice hash dell'oggetto di tipo Store 
    in base ai suoi campi id e name. Viene utilizzato il metodo statico Objects.hash() per calcolare il codice hash combinando 
    i valori dei campi id e name. Questo metodo restituisce un valore hash intero che rappresenta l'oggetto Store.*/


    @Override
    public String toString() {
        return "Store{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
