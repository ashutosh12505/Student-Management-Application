package com.practice.student_management;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.practice.student_management.exception.StudentNotFoundException;

@ExtendWith(MockitoExtension.class)
public class StudentServiceTest {
	
	@Mock
    StudentRepository repository;
	
	@InjectMocks
    StudentService service;

    @Test
    void getStudentById_shouldReturnStudent() {

        Student student = new Student();
        student.setId(1);
        student.setName("Ashutosh");
        student.setEmail("ashutosh@example.com");
        student.setAge(22);

        // Whenever the service asks the repository for student 1, pretend the database returned this student.
        when(repository.findById(1))
                .thenReturn(Optional.of(student));


        StudentDTO result = service.getStudentById(1);

        assertEquals(1, result.getId());
        assertEquals("Ashutosh", result.getName());
        assertEquals("ashutosh@example.com", result.getEmail());
        assertEquals(22, result.getAge());
    }
    
    @Test
    void getStudentById_shouldThrowExceptionWhenStudentDoesNotExist() {

        when(repository.findById(99))
                .thenReturn(Optional.empty());

        assertThrows(
        	    StudentNotFoundException.class,
        	    () -> service.getStudentById(99)
        );
    }
}