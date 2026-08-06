package com.campusone.campusone.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "users", uniqueConstraints = {
		@UniqueConstraint(name = "uk_users_registration_number", columnNames = "registration_number"),
		@UniqueConstraint(name = "uk_users_email", columnNames = "email")
})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, updatable = false)
	@Setter(AccessLevel.NONE)
	@EqualsAndHashCode.Include
	private Long id;

	@NotBlank
	@Pattern(regexp = "^[A-Za-z0-9]+$")
	@Size(max = 20)
	@Column(name = "registration_number", nullable = false, length = 20, unique = true)
	private String registrationNumber;

	@NotBlank
	@Size(max = 150)
	@Column(name = "full_name", nullable = false, length = 150)
	private String fullName;

	@NotBlank
	@Email
	@Pattern(regexp = "^[A-Za-z0-9]+@student\\.annauniv\\.edu$")
	@Size(max = 254)
	@Column(name = "email", nullable = false, length = 254, unique = true)
	private String email;

	@NotBlank
	@Size(min = 8, max = 255)
	@Column(name = "password", nullable = false, length = 255)
	private String password;

	@NotBlank
	@Size(max = 100)
	@Column(name = "department", nullable = false, length = 100)
	private String department;

	@NotNull
	@Min(1)
	@Column(name = "year", nullable = false)
	private Integer year;

	@NotBlank
	@Pattern(regexp = "^[0-9]{10,15}$")
	@Size(max = 15)
	@Column(name = "phone_number", nullable = false, length = 15)
	private String phoneNumber;

	@Column(name = "enabled", nullable = false)
	private boolean enabled = true;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	@Setter(AccessLevel.NONE)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at", nullable = false)
	@Setter(AccessLevel.NONE)
	private LocalDateTime updatedAt;

	@ToString.Exclude
	@JsonManagedReference("user-roles")
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "user_roles",
			joinColumns = @JoinColumn(name = "user_id", nullable = false),
			inverseJoinColumns = @JoinColumn(name = "role_id", nullable = false))
	private Set<Role> roles = new HashSet<>();

	@ToString.Exclude
	@JsonManagedReference("user-submitted-requests")
	@OneToMany(mappedBy = "requestedBy", fetch = FetchType.LAZY)
	private Set<EventAdminRequest> submittedRequests = new HashSet<>();

	@ToString.Exclude
	@JsonManagedReference("user-reviewed-requests")
	@OneToMany(mappedBy = "reviewedBy", fetch = FetchType.LAZY)
	private Set<EventAdminRequest> reviewedRequests = new HashSet<>();
}
