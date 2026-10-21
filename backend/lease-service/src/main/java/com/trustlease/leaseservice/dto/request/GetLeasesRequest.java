package com.trustlease.leaseservice.dto.request;

import com.trustlease.leaseservice.shared_contracts.LeasePermission;
import jakarta.validation.constraints.Positive;

public record GetLeasesRequest(
	@Positive Long organizationId,
	LeasePermission leaseStatus,
	@Positive Long subject_id
) {}
