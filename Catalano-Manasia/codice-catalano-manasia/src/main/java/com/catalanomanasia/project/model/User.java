package com.catalanomanasia.project.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@Entity
@Table(name="users")
public class User
{
    public User(){
        this.registeredStores = new HashSet<>();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 45)
    private String email;

    @Column(nullable = false, unique = true, length = 45)
    private String username;

    @Column(nullable = false, length = 64)
    private String password;

    @Column(name = "first_name", nullable = false, length = 20)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 20)
    private String lastName;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @ManyToOne(optional = true)
    @JoinColumn(name = "store_id", nullable = true)
    private Store store;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "registeredUsers", cascade = {CascadeType.MERGE})
    Set<Store> registeredStores;

    private Boolean enabled;

    public void addStore(Store s){
        this.registeredStores.add(s);
        s.getRegisteredUsers().add(this);
    }

    @Override
    public boolean equals(Object o) {
        // Se l'oggetto confrontato è lo stesso oggetto, ritorniamo true poiché sono uguali
        if (this == o) return true;

        // Se l'oggetto confrontato è null o non appartiene alla stessa classe, ritorniamo false poiché sono sicuramente diversi
        if (o == null || getClass() != o.getClass()) return false;

        // Eseguiamo un casting dell'oggetto confrontato in un oggetto di tipo 'User'
        User user = (User) o;

        // Confrontiamo i campi di 'this' (l'oggetto corrente) e 'user' (l'oggetto confrontato) utilizzando il metodo 'Objects.equals'
        // Ritorniamo 'true' solo se tutti i campi degli oggetti sono uguali
        return Objects.equals(id, user.id) &&
                Objects.equals(email, user.email) &&
                Objects.equals(username, user.username) &&
                Objects.equals(password, user.password) &&
                Objects.equals(firstName, user.firstName) &&
                Objects.equals(lastName, user.lastName) &&
                Objects.equals(role, user.role) &&
                Objects.equals(store, user.store) &&
                Objects.equals(enabled, user.enabled);
    }

    /*equals(): Il metodo equals() confronta l'oggetto corrente con un altro oggetto passato come parametro (o).
    Inizialmente, controlla se i due oggetti puntano alla stessa istanza di memoria (cioè sono lo stesso oggetto) usando this == o.
    Se sono lo stesso oggetto, ritorna true, poiché sono considerati uguali.

    Successivamente, il metodo verifica se l'oggetto passato come parametro è nullo o appartiene a una classe diversa rispetto a quella dell'oggetto corrente
    (getClass() != o.getClass()). Se l'oggetto è nullo o appartiene a una classe diversa, significa che non può essere uguale all'oggetto corrente,
    quindi ritorna false.

    Infine, il metodo esegue un casting dell'oggetto passato come parametro in un oggetto di tipo User e confronta i valori di tutti i campi
    dell'oggetto corrente (this) con quelli dell'oggetto passato (user). Utilizza Objects.equals() per eseguire confronti tra oggetti in modo sicuro e
    restituisce true solo se tutti i campi degli oggetti sono uguali.*/

    @Override
    public int hashCode() {
        // Calcoliamo un hash code basato sui valori degli ID, email, username, password, firstName, lastName, role, store, e enabled
        return Objects.hash(id, email, username, password, firstName, lastName, role, store, enabled);
    }

    /*hashCode(): Il metodo hashCode() calcola un valore hash basato sui valori degli ID, email, username, password, firstName, lastName, role, store, e enabled.
     Il valore hash viene calcolato utilizzando Objects.hash() che combina i valori dei campi per ottenere un hash code unico per ogni oggetto User.
     Questo hash code viene utilizzato dalle collezioni speciali come HashSet o HashMap per organizzare e cercare gli oggetti all'interno delle loro strutture
     dati interne.*/

    /*Implementare correttamente i metodi equals() e hashCode() è essenziale quando si lavora con collezioni speciali in Java, come HashSet, HashMap e
    altri tipi di collezioni basate su hash. Ciò è dovuto a due motivi principali:

     1- Garantire la coerenza tra equals() e hashCode(): Il contratto tra equals() e hashCode() richiede che due oggetti che sono uguali secondo il metodo
     equals() devono restituire lo stesso valore hash (hashCode()). Se questo contratto non è rispettato, gli oggetti potrebbero essere gestiti in
     modo imprevedibile all'interno delle collezioni. Ad esempio, se hai un oggetto User nel HashSet e successivamente modifichi uno dei campi che
     influisce sul risultato di equals(), ma non sul risultato di hashCode(), potresti non essere in grado di trovarlo più all'interno del set perché
     il suo hash code sarà diverso. Questo può portare a comportamenti indesiderati o errori di ricerca all'interno delle collezioni.

    2- Garantire la corretta funzionalità delle collezioni basate su hash: Le collezioni speciali in Java, come HashSet e HashMap,
    utilizzano il valore hash degli oggetti per organizzare ed effettuare ricerche rapide all'interno delle loro strutture dati interne.
    Un buon hash code distribuisce gli oggetti in modo uniforme all'interno della collezione, evitando collisioni e garantendo prestazioni ottimali.
    Se due oggetti uguali restituiscono hash code diversi, si verificheranno collisioni all'interno delle collezioni, con il rischio di rallentare
    notevolmente le operazioni di ricerca, inserimento ed eliminazione.

    In sintesi, garantire che i metodi equals() e hashCode() siano implementati correttamente e coerenti è fondamentale per garantire il corretto
    funzionamento delle collezioni speciali basate su hash e per evitare problemi di ricerca e duplicati all'interno delle strutture dati.*/


    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", role=" + role +
                ", store=" + store +
                ", enabled=" + enabled +
                '}';
    }
}