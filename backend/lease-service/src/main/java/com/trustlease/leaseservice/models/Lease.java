package com.trustlease.leaseservice.models;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.trustlease.leaseservice.shared_contracts.LeasePermission;
import com.trustlease.leaseservice.shared_contracts.LeaseStatus;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "leases")
public class Lease {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "org_id", nullable = false)
	private Long orgId;

	@Column(name = "resource_id", nullable = false)
	private Long resourceId;

	@Column(name = "subject_id", nullable = false)
	private Long subjectId;

	@ElementCollection
	@Enumerated(EnumType.STRING)
	@CollectionTable(name = "lease_permissions", joinColumns = @JoinColumn(name = "lease_id"), uniqueConstraints = @UniqueConstraint(columnNames = {
			"lease_id", "permission" }))
	@Column(name = "permission")
	private Set<LeasePermission> permissions = new HashSet<>();

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at")
	private Instant updatedAt;

	@Column(name = "expires_at")
	private Instant expiresAt;

	@Column(name = "max_uses")
	private Long maxUses;

	@Version
	private Long version;

	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private LeaseStatus status;

}