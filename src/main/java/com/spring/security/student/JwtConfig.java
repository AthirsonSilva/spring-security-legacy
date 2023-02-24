package com.spring.security.student;

import com.google.common.net.HttpHeaders;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties
@ConfigurationProperties(prefix = "application.jwt")
@Getter
@Setter
public class JwtConfig {
	private String secretKey;
	private String tokenPrefix;
	private Integer tokenExpirationAfterDays;

	public JwtConfig() {
	}

	public String getAuthorizationHeader() {
		return HttpHeaders.AUTHORIZATION;
	}
}
