package com.trustlease.leaseservice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.trustlease.leaseservice.dto.response.GetLeaseResponse;
import com.trustlease.leaseservice.dto.response.PageResponse;
import com.trustlease.leaseservice.models.Lease;
import com.trustlease.leaseservice.repository.LeaseRepository;

import lombok.RequiredArgsConstructor;

@Service
public class LeaseService {

	private final LeaseRepository leaseRepository;

	LeaseService(LeaseRepository leaseRepository) {
		this.leaseRepository = leaseRepository;
	}

	public PageResponse<GetLeaseResponse> getLeases(Pageable pageable) {
		Page<Lease> result = leaseRepository.findAll(pageable);
		List<GetLeaseResponse> items = result.getContent().stream().map(lease -> GetLeaseResponse.toDto(lease, null))
				.toList();

		return new PageResponse<>(items, result.getNumber(), result.getSize(), result.getTotalElements());
	}

}
