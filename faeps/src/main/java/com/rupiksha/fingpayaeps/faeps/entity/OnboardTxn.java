package com.rupiksha.fingpayaeps.faeps.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "onboard_txn")
@Getter
@Setter
public class OnboardTxn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String merchantLoginId;

    private Integer superMerchantId;

    private String txnId;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String remarks;

    @Column(columnDefinition = "LONGTEXT")
    private String requestPayload;

    @Column(columnDefinition = "LONGTEXT")
    private String responsePayload;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum Status {
        INIT, SENT, SUCCESS, FAILED
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}