package com.trustlease.leaseservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.trustlease.leaseservice.models.Lease;

public interface LeaseRepository extends JpaRepository<Lease, Long>{

	Page<Lease> findAll(Pageable pageable);

}
