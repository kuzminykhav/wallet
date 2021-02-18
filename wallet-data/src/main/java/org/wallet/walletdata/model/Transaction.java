package org.wallet.walletdata.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "transactions")
public class Transaction extends BaseEntity {

    @ManyToOne
    private User user;

    @ManyToOne
    private Wallet wallet;

    private BigDecimal amount;
    private BigDecimal startBalance;
    private BigDecimal endBalance;

    private String description;

    @Enumerated(value = EnumType.STRING)
    private Currency currency;

    private Boolean lastTransaction;

    @CreationTimestamp
    private LocalDateTime creationTimestamp;

}
