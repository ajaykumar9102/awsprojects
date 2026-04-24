package com.rupiksha.fingpayaeps.faeps.repository;


import com.rupiksha.fingpayaeps.faeps.entity.OnboardTxn;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OnboardTxnRepo extends JpaRepository<OnboardTxn, Long> {

    Optional<OnboardTxn> findByMerchantLoginId(String merchantLoginId);

    Optional<OnboardTxn> findTopByMerchantLoginIdOrderByCreatedAtDesc(String merchantLoginId);

    Optional<OnboardTxn> findByTxnId(String txnId);

    Optional<OnboardTxn> findTopByTxnIdOrderByCreatedAtDesc(String txnId);

    Page<OnboardTxn> findByStatus(OnboardTxn.Status status, Pageable pageable);
}