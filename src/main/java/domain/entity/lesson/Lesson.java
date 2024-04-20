package domain.entity.lesson;

import domain.entity.coach.Coach;
import domain.entity.learner.Learner;
import domain.entity.Review;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Lesson {
    private final String name;
    private int grade;
    private final Coach coach;
    private final LocalDate lessonDate;
    private final String lessonTime;
    private final List<Learner> registeredLearners;
    private final List<Review> reviews;
    private int id = -1;

    /**
     * Create a new instance of a lesson
     *
     * @param name               name of the lesson
     * @param grade              grade of the lesson
     * @param coach              coach
     * @param lessonDate         date
     * @param lessonTime         time of the lesson
     * @param registeredLearners pre-defined list of registered learners
     * @param reviews            pre-defined list of reviews
     */
    public Lesson(String name, int grade, Coach coach, LocalDate lessonDate, String lessonTime, List<Learner> registeredLearners, List<Review> reviews) {
        this.name = name;
        this.grade = grade;
        this.coach = coach;
        this.lessonDate = lessonDate;
        this.lessonTime = lessonTime;
        this.registeredLearners = new ArrayList<>();
        this.reviews = reviews;
        coach.assignLesson(this);

        if (!registeredLearners.isEmpty()) {
            this.registeredLearners.addAll(registeredLearners);
        }
    }

    /**
     * Create a new instance of a lesson
     *
     * @param name       name of the lesson
     * @param grade      grade of the lesson
     * @param coach      coach
     * @param lessonDate date
     * @param lessonTime time of the lesson
     */
    public Lesson(String name, int grade, Coach coach, LocalDate lessonDate, String lessonTime) {
        this(name, grade, coach, lessonDate, lessonTime, new ArrayList<>(), new ArrayList<>());
    }

    /**
     * Set id for this lesson
     *
     * @param id id
     * @return this lesson instance
     */
    public Lesson setId(int id) {
        this.id = id;
        return this;
    }

    /**
     * Add a new leaner to the registered learners
     *
     * @param learner learner
     */
    public void addLearner(Learner learner) {
        this.registeredLearners.add(learner);
    }

    /**
     * Add a new review for this lesson
     *
     * @param review the new review
     */
    public void addReview(Review review) {
        this.reviews.add(review);
    }

    /**
     * Get the name of this lesson
     *
     * @return the lesson name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the grade associated with the lesson.
     *
     * @return The grade value.
     */
    public int getGrade() {
        return grade;
    }


    /**
     * Retrieves the coach assigned to the lesson.
     *
     * @return The coach object.
     */
    public Coach getCoach() {
        return coach;
    }


    /**
     * Returns the date of the lesson.
     *
     * @return The lesson date.
     */
    public LocalDate getLessonDate() {
        return lessonDate;
    }

    /**
     * Get the time for this lesson
     *
     * @return the lesson time
     */
    public String getLessonTime() {
        return lessonTime;
    }

    /**
     * Retrieves a list of learners registered for the lesson.
     *
     * @return The list of registered learners.
     */
    public List<Learner> getRegisteredLearners() {
        return registeredLearners;
    }

    /**
     * Returns a list of reviews associated with the lesson.
     *
     * @return The list of reviews.
     */
    public List<Review> getReviews() {
        return reviews;
    }

    /**
     * Retrieves the unique identifier for the lesson.
     *
     * @return The lesson ID.
     * @throws IllegalStateException if the ID is not yet assigned.
     */
    public int getId() {
        if (id == -1) {
            throw new IllegalStateException("Lesson id not yet assigned");
        }
        return id;
    }

    /**
     * Set the grade for this lesson
     *
     * @param grade grade to be set
     */
    public void setGrade(int grade) {
        this.grade = grade;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lesson lesson = (Lesson) o;
        return id == lesson.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
