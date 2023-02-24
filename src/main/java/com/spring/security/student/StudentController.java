package com.spring.security.student;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/students")
public class StudentController {
	List<Student> students = List.of(
			new Student(UUID.randomUUID(), "Anna Smith")
	);

	@GetMapping(path = "{studentId}")
	public ResponseEntity<Student> getStudent(@PathVariable UUID studentId) {
		Student student = students.stream().filter(
						currentStudent -> currentStudent.getUuid().equals(studentId)
				).findFirst()
				.orElseThrow(
						() -> new IllegalStateException("Student " + studentId + " does not exists")
				);

		return new ResponseEntity<>(student, HttpStatus.OK);
	}

	@GetMapping
	public ResponseEntity<Map<String, Object>> getAllStudents() {
		return new ResponseEntity<>(
				Map.of(
						"students fetched!",
						students,
						"total",
						students.size()
				), HttpStatus.OK);
	}

	@PostMapping
	public ResponseEntity<Map<String, Student>> registerNewStudent(@RequestBody Student request) {
		Student newStudent = new Student();

		newStudent.setUuid(request.getUuid());
		newStudent.setName(request.getName());

		students.add(newStudent);

		return new ResponseEntity<>(
				Map.of(
						"Student registered!",
						newStudent
				), HttpStatus.CREATED);
	}


}
