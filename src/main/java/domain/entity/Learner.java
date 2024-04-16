package domain.entity;

import domain.entity.lesson.RegisteredLesson;

import java.util.ArrayList;
import java.util.List;

public class Learner {
    private String name;
    private String gender;
    private int age;
    private int grade;
    private String phoneNumber;
    private String emergencyContactNumber;
    private final List<RegisteredLesson> registeredLessons;
    private int id = -1;

    public Learner(String name, String gender, int age, int grade, String phoneNumber, String emergencyContactNumber, List<RegisteredLesson> registeredLessons) {
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.grade = grade;
        this.phoneNumber = phoneNumber;
        this.emergencyContactNumber = emergencyContactNumber;
        this.registeredLessons = registeredLessons;
    }

    public Learner(String name, String gender, int age, int grade, String phoneNumber, String emergencyContactNumber) {
        this(name, gender, age, grade, phoneNumber, emergencyContactNumber, new ArrayList<>());
    }

    public Learner setId(int id){
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public int getAge() {
        return age;
    }

    public int getGrade() {
        return grade;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmergencyContactNumber() {
        return emergencyContactNumber;
    }

    public List<RegisteredLesson> getRegisteredLessons() {
        return registeredLessons;
    }

    public int getId() {
        if (id == -1){
            throw new IllegalStateException("Learner id not yet assigned");
        }
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setEmergencyContactNumber(String emergencyContactNumber) {
        this.emergencyContactNumber = emergencyContactNumber;
    }
}
