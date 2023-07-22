package com.catalanomanasia.project.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transaction")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "credit_card_id", referencedColumnName = "id")

    private CreditCard creditCard;
    private String type;

    private Integer amount;
    private Date time;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(id, that.id) && Objects.equals(creditCard, that.creditCard) && type == that.type && Objects.equals(amount, that.amount) && Objects.equals(time, that.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, creditCard, type, amount, time);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", creditCard=" + creditCard +
                ", type=" + type +
                ", amount=" + amount +
                ", time=" + time +
                '}';
    }
}
