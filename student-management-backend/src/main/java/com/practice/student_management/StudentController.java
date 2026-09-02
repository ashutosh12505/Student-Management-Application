package com.practice.student_management;

import java.util.List;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

@RestController
public class StudentController {
	

	private StudentService studentService;
	
	public StudentController(StudentService studentService) {
		this.studentService = studentService;
	}
	

    @GetMapping("/students")
    public List<StudentDTO> getStudents() {
//    	System.out.println("getAll called in controller");
        return studentService.getAllStudents();
    }

    @PostMapping("/students")
    public ResponseEntity<StudentDTO> addStudent(
            @Valid @RequestBody CreateStudentRequestDTO request) {

//        System.out.println("addStudent called in controller");
        StudentDTO savedStudent = studentService.addStudent(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedStudent);
    }
    
    @GetMapping("/students/{id}")
    public ResponseEntity<StudentDTO> getStudentById(@PathVariable int id) {

//        System.out.println("getById called in controller");
        StudentDTO studentDTO = studentService.getStudentById(id);

        return ResponseEntity.ok(studentDTO);
    }
    

    @PutMapping("/students/{id}")
    public ResponseEntity<StudentDTO> updateStudent(
            @PathVariable int id,
            @Valid @RequestBody UpdateStudentRequestDTO request) {

//        System.out.println("updateStudent called in controller");

        StudentDTO updatedStudent =
                studentService.updateStudent(id, request);

        return ResponseEntity.ok(updatedStudent);
    }
    
    @DeleteMapping("/students/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable int id) {

//        System.out.println("deleteStudent called in controller");
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }
    
//    @GetMapping("/students/transaction-test")
//    public ResponseEntity<String> transactionTest() {
//
//        studentService.transactionTest();
//
//        return ResponseEntity.ok("Transaction completed");
//    }
}