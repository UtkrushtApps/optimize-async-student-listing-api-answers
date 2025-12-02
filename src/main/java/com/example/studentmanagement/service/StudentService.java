package com.example.studentmanagement.service;

import com.example.studentmanagement.dto.CourseDTO;
import com.example.studentmanagement.dto.PageResponse;
import com.example.studentmanagement.dto.StudentDTO;
import com.example.studentmanagement.entity.Student;
import com.example.studentmanagement.repository.StudentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SettableListenableFuture;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class StudentService {
    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);

    private final StudentRepository studentRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public PageResponse<StudentDTO> getPaginatedStudents(int page, int size) {
        if (page < 0 || size <= 0) {
            logger.warn("Invalid page/size parameters requested: page={}, size={}", page, size);
            throw new IllegalArgumentException("Page index must not be negative and size must be positive.");
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<Student> studentPage = studentRepository.findAll(pageable);
        List<StudentDTO> dtos = studentPage.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        return new PageResponse<>(
                dtos,
                studentPage.getNumber(),
                studentPage.getSize(),
                studentPage.getTotalElements(),
                studentPage.getTotalPages(),
                studentPage.isLast()
        );
    }

    @Async
    public CompletableFuture<Long> calculateStudentStatisticsSlowly() {
        try {
            logger.info("Slow statistics calculation started");
            Thread.sleep(5000); // Simulate slow operation
            long count = studentRepository.count();
            logger.info("Slow statistics calculation finished: {} students", count);
            return CompletableFuture.completedFuture(count);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Async statistics calculation interrupted", e);
            throw new RuntimeException("Calculation interrupted");
        }
    }

    private StudentDTO mapToDTO(Student student) {
        List<CourseDTO> courses = student.getCourses().stream()
                .map(course -> new CourseDTO(course.getId(), course.getTitle()))
                .collect(Collectors.toList());
        return new StudentDTO(student.getId(), student.getName(), courses);
    }
}
