package com.campusone.campusone.entity;

import java.time.LocalDateTime;

import com.campusone.campusone.entity.enums.AppAdminRequestStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "app_admin_requests")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AppAdminRequest {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, updatable = false)
	@Setter(AccessLevel.NONE)
	@EqualsAndHashCode.Include
	private Long id;

	@NotBlank
	@Size(max = 2000)
	@Column(name = "request_reason", nullable = false, length = 2000)
	private String requestReason;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 20)
	@NotNull
	private AppAdminRequestStatus status = AppAdminRequestStatus.PENDING;

	@CreationTimestamp
	@Column(name = "requested_at", nullable = false, updatable = false)
	@Setter(AccessLevel.NONE)
	private LocalDateTime requestedAt;

	@Column(name = "reviewed_at")
	private LocalDateTime reviewedAt;

	@Size(max = 2000)
	@Column(name = "remarks", length = 2000)
	private String remarks;

	@ToString.Exclude
	@JsonBackReference("app-admin-request-requested-by")
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "requested_by_user_id", nullable = false)
	@NotNull
	private User requestedBy;

	@ToString.Exclude
	@JsonBackReference("app-admin-request-reviewed-by")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "reviewed_by_user_id")
	private User reviewedBy;
}
