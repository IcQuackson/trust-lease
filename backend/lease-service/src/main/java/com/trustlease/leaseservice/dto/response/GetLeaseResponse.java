package com.trustlease.leaseservice.dto.response;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import com.trustlease.leaseservice.models.Lease;
import com.trustlease.leaseservice.shared_contracts.LeasePermission;
import com.trustlease.leaseservice.shared_contracts.LeaseStatus;

public record GetLeaseResponse(
		Long id,
		Long orgId,
		Long resourceId,
		Long subjectId,
		Set<LeasePermission> permissions,
		Instant expiresAt,
		Long maxUses,
		Long remainingUses,
		LeaseStatus status,
		Long version) {
	public static GetLeaseResponse toDto(Lease lease, Long remainingUses) {
		return new GetLeaseResponse(lease.getId(), lease.getOrgId(), lease.getResourceId(), lease.getSubjectId(),
				lease.getPermissions(), lease.getExpiresAt(), lease.getMaxUses(), remainingUses, lease.getStatus(),
				lease.getVersion());
	}
}
