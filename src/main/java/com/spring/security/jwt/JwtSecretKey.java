package com.spring.security.jwt;

import com.spring.security.student.JwtConfig;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;

@EnableConfigurationProperties(JwtConfig.class)
@Configuration
public class JwtSecretKey {
	private final JwtConfig jwtConfig;

	public JwtSecretKey(JwtConfig jwtConfig) {
		this.jwtConfig = jwtConfig;
	}

	@Bean
	public SecretKey getSecretKeyForSigning() {
		return Keys.hmacShaKeyFor(jwtConfig.getSecretKey().getBytes());
	}
}
