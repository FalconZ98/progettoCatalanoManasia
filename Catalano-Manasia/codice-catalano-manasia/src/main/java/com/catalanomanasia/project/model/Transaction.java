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
    @Override // Indica che stiamo sovrascrivendo il metodo 'equals' della classe Object
    public boolean equals(Object o) {
        // Se l'oggetto confrontato è lo stesso oggetto, ritorniamo true poiché sono uguali
        if (this == o) return true;

        // Se l'oggetto confrontato è null o non appartiene alla stessa classe, ritorniamo false poiché sono sicuramente diversi
        if (o == null || getClass() != o.getClass()) return false;

        // Eseguiamo un casting dell'oggetto confrontato in un oggetto di tipo 'Transaction'
        Transaction that = (Transaction) o;

        // Confrontiamo i campi di 'this' e 'that' (l'oggetto confrontato) utilizzando il metodo 'Objects.equals'
        // Ritorniamo 'true' se gli ID, le carte di credito, il tipo di transazione, l'importo e il tempo sono tutti uguali
        return Objects.equals(id, that.id) &&
                Objects.equals(creditCard, that.creditCard) &&
                type == that.type &&
                Objects.equals(amount, that.amount) &&
                Objects.equals(time, that.time);
    }

    @Override
    public int hashCode() {
        // Calcoliamo un hash code basato sui valori degli ID, delle carte di credito, del tipo di transazione, dell'importo e del tempo
        return Objects.hash(id, creditCard, type, amount, time);

        /*Il metodo hashCode() viene utilizzato in congiunzione con il metodo equals() per garantire il corretto funzionamento di strutture dati come HashSet,
        HashMap e altre collezioni basate sulla ricerca e l'identificazione degli oggetti.

        Quando si aggiungono oggetti in una struttura dati come HashSet o HashMap, queste strutture utilizzano l'hash code di ciascun oggetto per
        posizionarlo all'interno di una posizione specifica (bucket) nell'archiviazione interna. In seguito, quando si cerca di recuperare o confrontare
        gli oggetti all'interno di queste strutture, vengono utilizzati sia l'hash code che il metodo equals().

        Senza l'implementazione corretta del metodo hashCode(), oggetti che sono logicamente uguali (cioè hanno lo stesso valore nei loro campi)
        ma hanno differenti hash code, potrebbero finire in posizioni diverse nella struttura dati, rendendo difficile o impossibile trovarli o riconoscerli
        come duplicati. Inoltre, la mancanza di una corretta implementazione di hashCode() potrebbe comportare problemi nelle strutture dati che si basano
        su questa funzione, producendo comportamenti imprevisti o inefficienze.

        Il contratto tra equals() e hashCode() richiede che due oggetti uguali (ovvero gli oggetti per i quali il metodo equals() ritorna true)
        devono avere lo stesso valore restituito dal metodo hashCode(). Al contrario, due oggetti che hanno lo stesso valore restituito dal metodo hashCode()
        non sono necessariamente uguali (potrebbero essere degli hash code duplicati), quindi il metodo equals() deve essere utilizzato per risolvere
        eventuali collisioni.

        In sintesi, l'implementazione corretta del metodo hashCode() è fondamentale per garantire il corretto funzionamento delle strutture dati basate
        sugli hash e per garantire che oggetti logicamente uguali siano riconosciuti come tali.*/

    }

    @Override
    public String toString() {
        // Restituiamo una rappresentazione in formato stringa dell'oggetto Transaction
        return "Transaction{" +
                "id=" + id +
                ", creditCard=" + creditCard +
                ", type=" + type +
                ", amount=" + amount +
                ", time=" + time +
                '}';
    }
}
