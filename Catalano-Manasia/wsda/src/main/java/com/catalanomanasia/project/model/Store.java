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

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "Store{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
