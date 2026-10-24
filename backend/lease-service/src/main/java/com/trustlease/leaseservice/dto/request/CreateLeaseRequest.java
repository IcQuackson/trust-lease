package com.trustlease.leaseservice.dto.request;

import java.time.Instant;
import java.util.Set;

import org.hibernate.annotations.Changelog.Timestamp;

import com.trustlease.leaseservice.shared_contracts.LeasePermission;
import com.trustlease.leaseservice.shared_contracts.LeaseStatus;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.Positive;

public record CreateLeaseRequest(
	@Nonnull @Positive Long orgId,
	@Positive  Long resourceId,
	@Nonnull @Positive Long subjectId,
	@Nonnull Set<LeasePermission> permissions,
	@Nonnull @Timestamp Instant expiresAt,
	@Nonnull @Positive Long maxUses
) {}
