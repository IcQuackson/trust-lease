package com.trustlease.leaseservice.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.trustlease.leaseservice.dto.request.GetLeasesRequest;
import com.trustlease.leaseservice.dto.response.LeaseResponseDto;
import com.trustlease.leaseservice.dto.response.PageResponse;
import com.trustlease.leaseservice.service.LeaseService;
import com.trustlease.leaseservice.shared_contracts.LeasePermission;

import jakarta.validation.Valid;

import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

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
	public PageResponse<LeaseResponseDto> getLeases(
		@Valid @ModelAttribute GetLeasesRequest request,
		@PageableDefault(
			page = 0,
			size = 20,
			sort = "createdAt",
			direction = Direction.DESC
		)
		Pageable pageable) {
		return leaseService.getLeases(pageable);
	}

	@GetMapping("/{id}")
	public LeaseResponseDto getLease(@PathVariable Long id) {
		return leaseService.getLease(id);
	}

}
