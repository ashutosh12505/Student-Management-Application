package com.practice.student_management;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.practice.student_management.exception.StudentNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;

@Service
public class StudentService {

    private StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Transactional(readOnly = true)
    public List<StudentDTO> getAllStudents() {
//        System.out.println("getAll called in service");
        List<Student> students = studentRepository.findAll();

        return students.stream()
                .map(student -> new StudentDTO(
                        student.getId(),
                        student.getName(),
                        student.getEmail(),
                        student.getAge()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public StudentDTO getStudentById(int id) {
//    	System.out.println("getById called in service");
//        return studentRepository.findById(id).orElse(null);
    	
//    	int x = 10/0;
    	
    	Student student = studentRepository.findById(id).orElse(null);
    	if(student == null) {
    		throw new StudentNotFoundException("Student not found with id: " + id);
    	}
    	
    	StudentDTO dto = new StudentDTO(student.getId(), student.getName(), student.getEmail(), student.getAge());
    	return dto;
    }

    @Transactional
    public StudentDTO addStudent(CreateStudentRequestDTO request) {

//        System.out.println("addStudent called in service");
        Student student = new Student();
        student.setName(request.getName());
        student.setEmail(request.getEmail());
        student.setAge(request.getAge());
        Student savedStudent = studentRepository.save(student);
        return new StudentDTO(
                savedStudent.getId(),
                savedStudent.getName(),
                savedStudent.getEmail(),
                savedStudent.getAge()
        );
    }

    @Transactional
    public StudentDTO updateStudent(int id, UpdateStudentRequestDTO request) {

//        System.out.println("updateStudent called in service");
        Student existingStudent = studentRepository.findById(id).orElse(null);

        if (existingStudent == null) {
            throw new StudentNotFoundException("Student not found with id: " + id);
        }

        existingStudent.setName(request.getName());
        existingStudent.setEmail(request.getEmail());
        existingStudent.setAge(request.getAge());
        Student updatedStudent = studentRepository.save(existingStudent);
        return new StudentDTO(
                updatedStudent.getId(),
                updatedStudent.getName(),
                updatedStudent.getEmail(),
                updatedStudent.getAge()
        );
    }
    
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public boolean deleteStudent(int id) {

//        System.out.println("deleteStudent called in service");

        if (!studentRepository.existsById(id)) {
            throw new StudentNotFoundException("Student not found with id: " + id);
        }

        studentRepository.deleteById(id);
        return true;
    }
    
//    @Transactional
//    public void transactionTest() {
//
//        Student student = new Student();
//
//        student.setName("Transaction Test");
//
//        studentRepository.save(student);
//
//        System.out.println(
//                "Student saved, now throwing exception..."
//        );
//
//        throw new RuntimeException(
//                "Something went wrong!"
//        );
//    }
}