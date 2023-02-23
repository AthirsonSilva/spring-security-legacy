package com.spring.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.concurrent.TimeUnit;

import static com.spring.security.config.ApplicationUserPermission.COURSE_WRITE;
import static com.spring.security.config.ApplicationUserRole.*;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {
	private final PasswordEncoder passwordEncoder;

	@Autowired
	public ApplicationSecurityConfig(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	@Bean
	protected UserDetailsService userDetailsService() {
		UserDetails user = User
				.builder()
				.username("user")
				.password(passwordEncoder.encode("password"))
				.roles(STUDENT.name())
				.authorities(STUDENT.getGrantedAuthorities())
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
				.authorizeRequests()
				.antMatchers("/", "index", "/css/*", "/js/*")
					.permitAll()
				.antMatchers("/api/v1/students/**")
					.hasRole(STUDENT.name())
				.antMatchers(HttpMethod.POST, "/api/v1/management/**")
					.hasAuthority(COURSE_WRITE.getPermission())
				.antMatchers(HttpMethod.PUT, "/api/v1/management/**")
					.hasAuthority(COURSE_WRITE.getPermission())
				.antMatchers(HttpMethod.DELETE, "/api/v1/management/**")
					.hasAuthority(COURSE_WRITE.getPermission())
				.antMatchers(HttpMethod.GET, "/api/v1/management/**")
					.hasAnyRole(ADMIN.name(), TRAINEE.name())
				.anyRequest()
					.authenticated()
				.and()
				.formLogin()
					.loginPage("/login")
					.permitAll()
					.defaultSuccessUrl("/courses", true)
					.passwordParameter("password")
					.usernameParameter("username")
				.and()
				.rememberMe()
					.tokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(1))
					.key("secret")
					.rememberMeParameter("remember-me")
				.and()
				.logout()
					.logoutUrl("/logout")
					.logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
					.clearAuthentication(true)
					.invalidateHttpSession(true)
					.deleteCookies("JSESSIONID", "remember-me")
					.logoutSuccessUrl("/login");
	}
}
