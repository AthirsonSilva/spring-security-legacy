package com.spring.security.config;

import com.spring.security.auth.ApplicationUserService;
import com.spring.security.jwt.JwtTokenVerifier;
import com.spring.security.jwt.JwtUsernameAndPasswordAuthenticationFilter;
import com.spring.security.student.JwtConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import javax.crypto.SecretKey;

import static com.spring.security.config.ApplicationUserRole.*;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {
	private final PasswordEncoder passwordEncoder;

	private final ApplicationUserService applicationUserService;
	private final JwtConfig jwtConfig;
	private final SecretKey jwtSecretKey;
	private final SecretKey secretKey;

	@Autowired
	public ApplicationSecurityConfig(PasswordEncoder passwordEncoder, ApplicationUserService applicationUserService, JwtConfig jwtConfig, SecretKey jwtSecretKey, SecretKey secretKey) {
		this.passwordEncoder = passwordEncoder;
		this.applicationUserService = applicationUserService;
		this.jwtConfig = jwtConfig;
		this.jwtSecretKey = jwtSecretKey;
		this.secretKey = secretKey;
	}

	@Override
	@Bean
	protected UserDetailsService userDetailsService() {
		UserDetails user = User
				.builder()
				.username("user")
				.password(passwordEncoder.encode("password"))
				.roles(USER.name())
				.authorities(USER.getGrantedAuthorities())
				.build();

		UserDetails admin = User
				.builder()
				.username("admin")
				.password(passwordEncoder.encode("password"))
				.roles(ADMIN.name())
				.authorities(ADMIN.getGrantedAuthorities())
				.build();

		UserDetails trainee = User
				.builder()
				.username("trainee")
				.password(passwordEncoder.encode("password"))
				.roles(TRAINEE.name())
				.authorities(TRAINEE.getGrantedAuthorities())
				.build();

		return new InMemoryUserDetailsManager(user, admin, trainee);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
				.csrf().disable()
				.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and()
				.addFilter(new JwtUsernameAndPasswordAuthenticationFilter(authenticationManager(), jwtConfig, jwtSecretKey))
				.addFilterAfter(new JwtTokenVerifier(jwtConfig, secretKey), JwtUsernameAndPasswordAuthenticationFilter.class)
				.authorizeRequests()
				.antMatchers("/", "index", "/css/*", "/js/*").permitAll()
				.antMatchers(HttpMethod.GET, "/api/v1/management/**").hasAnyRole(
						ADMIN.name(),
						TRAINEE.name()
				)
				.antMatchers(HttpMethod.POST, "/api/v1/management/**").hasAnyAuthority(
						"course:write",
						"student:write"
				)
				.antMatchers("/api/v1/**").hasRole(USER.name())
				.anyRequest()
				.authenticated();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(daoAuthenticationProvider());
	}

	@Bean
	public DaoAuthenticationProvider daoAuthenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();

		provider.setPasswordEncoder(passwordEncoder);
		provider.setUserDetailsService(applicationUserService);

		return provider;
	}
}
