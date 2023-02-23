package com.spring.security.config;

import com.google.common.collect.Sets;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

public enum ApplicationUserRole {
	STUDENT(Sets.newHashSet()),
	ADMIN(Sets.newHashSet(
			ApplicationUserPermission.COURSE_READ,
			ApplicationUserPermission.COURSE_WRITE,
			ApplicationUserPermission.USER_READ,
			ApplicationUserPermission.USER_WRITE
	)),
	TRAINEE(Sets.newHashSet(
			ApplicationUserPermission.COURSE_READ,
			ApplicationUserPermission.USER_READ
	));

	private final Set<ApplicationUserPermission> permissions;

	ApplicationUserRole(Set<ApplicationUserPermission> permissions) {
		this.permissions = permissions;
	}

	public Set<ApplicationUserPermission> getPermissions() {
		return permissions;
	}

	public Set<SimpleGrantedAuthority> getGrantedAuthorities() {
		Set<SimpleGrantedAuthority> permissions = getPermissions()
				.stream()
				.map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
				.collect(Collectors.toSet());

		permissions.add(new SimpleGrantedAuthority("ROLE_" + this.name().toUpperCase()));

		return permissions;
	}
}
