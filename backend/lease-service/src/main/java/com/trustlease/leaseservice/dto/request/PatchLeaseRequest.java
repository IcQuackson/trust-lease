package com.trustlease.leaseservice.dto.request;

import java.time.Instant;
import java.util.Set;

import org.hibernate.annotations.Changelog.Timestamp;

import com.trustlease.leaseservice.shared_contracts.LeasePermission;

import jakarta.validation.constraints.Positive;

public record PatchLeaseRequest (
	Set<LeasePermission> permissions,
	@Timestamp Instant expiresAt
) {}
