package com.example.studentmanagement.dto;

import java.util.List;

public class StudentDTO {
    private Long id;
    private String name;
    private List<CourseDTO> courses;

    public StudentDTO() {}

    public StudentDTO(Long id, String name, List<CourseDTO> courses) {
        this.id = id;
        this.name = name;
        this.courses = courses;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public List<CourseDTO> getCourses() {
        return courses;
    }
    public void setCourses(List<CourseDTO> courses) {
        this.courses = courses;
    }
}
