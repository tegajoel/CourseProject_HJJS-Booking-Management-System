package domain.entity.lesson;

import domain.entity.Coach;
import domain.entity.Learner;
import domain.entity.Review;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Lesson {
    private final String name;
    private final int grade;
    private final Coach coach;
    private final LocalDate lessonDate;
    private final List<Learner> registeredLearners;
    private final List<Review> reviews;
    private int id = -1;

    public Lesson(String name, int grade, Coach coach, LocalDate lessonDate, List<Learner> registeredLearners, List<Review> reviews) {
        this.name = name;
        this.grade = grade;
        this.coach = coach;
        this.lessonDate = lessonDate;
        this.registeredLearners = registeredLearners;
        this.reviews = reviews;
    }

    public Lesson(String name, int grade, Coach coach, LocalDate lessonDate) {
        this(name, grade, coach, lessonDate, new ArrayList<>(), new ArrayList<>());
    }

    public Lesson setId(int id){
        this.id = id;
        return this;
    }

    public void addLearner(Learner learner){
        this.registeredLearners.add(learner);
    }

    public void addReview(Review review){
        this.reviews.add(review);
    }

    public String getName() {
        return name;
    }

    public int getGrade() {
        return grade;
    }

    public Coach getCoach() {
        return coach;
    }

    public LocalDate getLessonDate() {
        return lessonDate;
    }

    public List<Learner> getRegisteredLearners() {
        return registeredLearners;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public int getId() {
        if (id == -1){
            throw new IllegalStateException("Lesson id not yet assigned");
        }
        return id;
    }
}
