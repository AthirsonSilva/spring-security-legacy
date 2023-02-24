package com.spring.security.auth;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.spring.security.config.ApplicationUserRole.*;

@Repository("fake")
public class FakeApplicationUserDaoService implements ApplicationUserDao {

	private final PasswordEncoder passwordEncoder;

	@Autowired
	public FakeApplicationUserDaoService(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public Optional<ApplicationUser> selectApplicationUserByUsername(String username) {
		return getApplicationUsers()
				.stream()
				.filter(applicationUser -> username.equals(applicationUser.getUsername()))
				.findFirst();
	}

	private List<ApplicationUser> getApplicationUsers() {
		return Lists.newArrayList(
				new ApplicationUser(
						"user",
						passwordEncoder.encode("password"),
						USER.getGrantedAuthorities(),
						true,
						true,
						true,
						true
				),
				new ApplicationUser(
						"admin",
						passwordEncoder.encode("password"),
						ADMIN.getGrantedAuthorities(),
						true,
						true,
						true,
						true
				),
				new ApplicationUser(
						"trainee",
						passwordEncoder.encode("password"),
						TRAINEE.getGrantedAuthorities(),
						true,
						true,
						true,
						true
				)
		);
	}
}
