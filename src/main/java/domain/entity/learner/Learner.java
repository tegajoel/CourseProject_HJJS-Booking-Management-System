package domain.entity.learner;

import domain.entity.lesson.Lesson;
import domain.entity.lesson.LessonStatus;
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
        this.registeredLessons = new ArrayList<>();
        if (!registeredLessons.isEmpty()){
            // this allows items to be added via List.Of()/Arrays.asList()
            this.registeredLessons.addAll(registeredLessons);
        }
    }

    public Learner(String name, String gender, int age, int grade, String phoneNumber, String emergencyContactNumber) {
        this(name, gender, age, grade, phoneNumber, emergencyContactNumber, new ArrayList<>());
    }

    public Learner setId(int id) {
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
        if (id == -1) {
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

    /**
     * Update the status of the registered lesson
     *
     * @param lesson    lesson
     * @param newStatus new status
     */
    public void updateRegisteredLessonStatus(Lesson lesson, LessonStatus newStatus) {
        for (RegisteredLesson lsn : registeredLessons) {
            if (lsn.getLesson().equals(lesson)) {
                lsn.setLessonStatus(newStatus);
                break;
            }
        }
    }

    /**
     * Register a new lesson
     * @param lesson registered lesson
     */
    public void registerNewLesson(RegisteredLesson lesson){
        registeredLessons.add(lesson);
    }

    /**
     * Check if a user has registered for a lesson
     *
     * @param lesson lesson
     * @return true if registered, false otherwise
     */
    public boolean hasLessonRegistered(Lesson lesson) {
        for (RegisteredLesson lsn : registeredLessons) {
            if (lsn.getLesson().equals(lesson)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Get the lesson status of a registered lesson
     *
     * @param lesson the lesson to check
     * @return the lesson status if the lesson has been registered, null otherwise
     */
    public LessonStatus getLessonStatus(Lesson lesson) {
        for (RegisteredLesson lsn : registeredLessons) {
            if (lsn.getLesson().equals(lesson)) {
                return lsn.getLessonStatus();
            }
        }
        return null;
    }
}
