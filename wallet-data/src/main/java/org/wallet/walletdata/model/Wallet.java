package org.wallet.walletdata.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "wallets")
public class Wallet extends BaseEntity{

    private String name;

    @ManyToOne
    private User user;

    @Enumerated(value = EnumType.STRING)
    private Currency currency;

    @OneToMany(cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            mappedBy = "wallet")
    private Set<Transaction> transactions = new HashSet<>();

    public Wallet(User user, Currency currency) {
        this.user = user;
        this.currency = currency;
    }

}
