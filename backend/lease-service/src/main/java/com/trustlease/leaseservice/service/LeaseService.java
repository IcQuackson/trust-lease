package com.trustlease.leaseservice.service;

import java.security.Permission;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Set;

import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.trustlease.leaseservice.dto.request.CreateLeaseRequest;
import com.trustlease.leaseservice.dto.request.PatchLeaseRequest;
import com.trustlease.leaseservice.dto.response.LeaseResponseDto;
import com.trustlease.leaseservice.dto.response.PageResponse;
import com.trustlease.leaseservice.models.Lease;
import com.trustlease.leaseservice.repository.LeaseRepository;
import com.trustlease.leaseservice.shared_contracts.LeasePermission;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
public class LeaseService {

	private final LeaseRepository leaseRepository;

	LeaseService(LeaseRepository leaseRepository) {
		this.leaseRepository = leaseRepository;
	}

	public PageResponse<LeaseResponseDto> getLeases(Pageable pageable) {
		Page<Lease> result = leaseRepository.findAll(pageable);
		List<LeaseResponseDto> items = result.getContent().stream().map(lease -> LeaseResponseDto.toDto(lease, null))
				.toList();
		return new PageResponse<>(items, result.getNumber(), result.getSize(), result.getTotalElements());
	}

	public LeaseResponseDto getLease(Long leaseId) {
		Lease lease = leaseRepository.findById(leaseId).orElseThrow(() -> new ResourceNotFoundException(leaseId.toString()));
		return LeaseResponseDto.toDto(lease, null);
	}

	public LeaseResponseDto createLease(CreateLeaseRequest request) {
		
		Lease newLease = new Lease();
		newLease.setOrgId(request.orgId());
		newLease.setResourceId(request.resourceId());
		newLease.setSubjectId(request.subjectId());
		newLease.setPermissions(request.permissions());
		newLease.setExpiresAt(request.expiresAt());
		newLease.setMaxUses(request.maxUses());

		Lease savedLease = leaseRepository.save(newLease);

		return LeaseResponseDto.toDto(savedLease, null);
	}

	public void patchLease(PatchLeaseRequest request, Long leaseId) {

		Lease lease = leaseRepository.findById(leaseId).orElseThrow(() -> new ResourceNotFoundException(leaseId.toString()));

		Instant newExpiresAt = request.expiresAt();
		Set<LeasePermission> newPermissions = request.permissions();

		if (newExpiresAt != null && newExpiresAt.isBefore(lease.getExpiresAt()) && newExpiresAt.isAfter(Instant.now())) {
			lease.setExpiresAt(newExpiresAt);
			leaseRepository.save(lease);
		}

		if (lease.getPermissions().containsAll(newPermissions)) {
			lease.setPermissions(newPermissions);
			leaseRepository.save(lease);
		}
	}
}
