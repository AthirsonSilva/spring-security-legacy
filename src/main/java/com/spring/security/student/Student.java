package com.spring.security.student;

import lombok.*;

import java.util.UUID;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Student {
	private UUID uuid;
	private String name;
}
