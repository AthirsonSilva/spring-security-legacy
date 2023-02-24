package com.spring.security.student;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("api/v1/management")
public class StudentManagementController {
	List<Student> students = List.of(
			new Student(UUID.randomUUID(), "James Bond")
	);

	@GetMapping
	@PreAuthorize("hasAnyAuthority('user:read', 'course:read')")
	public ResponseEntity<Map<String, List<Student>>> getAllStudents() {
		return new ResponseEntity<>(Map.of(
				"Students",
				students
		), HttpStatus.OK);
	}

	@PostMapping
	@PreAuthorize("hasAnyAuthority('user:write', 'course:write')")
	public ResponseEntity<Map<String, Student>> registerNewStudent(@RequestBody Student request) {
		Student student = new Student(
				request.getUuid(),
				request.getName()
		);

		return new ResponseEntity<>(Map.of(
				"Student",
				student
		), HttpStatus.OK);
	}

	@DeleteMapping(path = "/{studentId}")
	public ResponseEntity<Map<String, String>> deleteStudent(@PathVariable UUID studentId) {
		Student student = students.stream().filter(
						currentStudent -> currentStudent.getUuid().equals(studentId)
				).findFirst()
				.orElseThrow(
						() -> new IllegalStateException("Student " + studentId + " does not exists")
				);

		students.remove(student);

		return new ResponseEntity<>(Map.of("message", "Student deleted!"), HttpStatus.NO_CONTENT);
	}

	@PatchMapping(path = "/{studentId}")
	public ResponseEntity<Map<String, String>> updateStudent(@PathVariable String studentId, @RequestBody Student request) {
		Student student = students.stream().filter(
						currentStudent -> Objects.equals(studentId, currentStudent.getUuid())
				).findFirst()
				.orElseThrow(
						() -> new IllegalStateException("Student " + studentId + " does not exists")
				);

		student.setName(request.getName());

		return new ResponseEntity<>(Map.of("message", "Student updated!"), HttpStatus.OK);
	}
}
