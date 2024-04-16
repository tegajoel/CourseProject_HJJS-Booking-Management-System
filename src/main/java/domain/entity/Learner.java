package domain.entity;

import java.util.ArrayList;
import java.util.List;

public class Learner {
    private String name;
    private String gender;
    private final int age;
    private int grade;
    private long phoneNumber;
    private long emergencyContactNumber;
    private final List<Lesson> registeredLessons;
    private int id = -1;

    public Learner(String name, String gender, int age, int grade, long phoneNumber, long emergencyContactNumber, List<Lesson> registeredLessons) {
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.grade = grade;
        this.phoneNumber = phoneNumber;
        this.emergencyContactNumber = emergencyContactNumber;
        this.registeredLessons = registeredLessons;
    }

    public Learner(String name, String gender, int age, int grade, long phoneNumber, long emergencyContactNumber) {
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

    public long getPhoneNumber() {
        return phoneNumber;
    }

    public long getEmergencyContactNumber() {
        return emergencyContactNumber;
    }

    public List<Lesson> getRegisteredLessons() {
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

    public void setPhoneNumber(long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setEmergencyContactNumber(long emergencyContactNumber) {
        this.emergencyContactNumber = emergencyContactNumber;
    }
}
