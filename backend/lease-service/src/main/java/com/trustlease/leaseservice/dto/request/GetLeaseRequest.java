package com.trustlease.leaseservice.dto.request;

import com.trustlease.leaseservice.shared_contracts.LeasePermission;
import jakarta.validation.constraints.Positive;

public record GetLeaseRequest(
	@Positive Long leaseId
) {}
