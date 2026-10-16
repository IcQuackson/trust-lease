package com.trustlease.leaseservice.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.trustlease.leaseservice.dto.response.GetLeaseResponse;
import com.trustlease.leaseservice.dto.response.PageResponse;
import com.trustlease.leaseservice.service.LeaseService;
import com.trustlease.leaseservice.shared_contracts.LeasePermission;

import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/v1/leases")
public class LeaseController {

	private final LeaseService leaseService;

	LeaseController(LeaseService leaseService) {
		this.leaseService = leaseService;
	}

	@GetMapping("/health")
	public Map<String, String> health() {
		return Map.of(
				"status", "UP",
				"service", "lease-service");
	}

	@GetMapping("/")
	public PageResponse<GetLeaseResponse> getLeases(
		@RequestParam(required = false) Long organizationId,
		@RequestParam(required = false) LeasePermission leaseStatus,
		@RequestParam(required = false) Long subject_id,
		@PageableDefault(
			page = 0,
			size = 20,
			sort = "createdAt",
			direction = Direction.DESC
		)
		Pageable pageable) {
		return leaseService.getLeases(pageable);
	}

}
