package domain.entity.coach;

import domain.entity.lesson.Lesson;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Coach {
    private String name;
    private final List<Lesson> assignedLessons;

    public Coach(String name, List<Lesson> assignedLessons) {
        this.name = name;
        this.assignedLessons = new ArrayList<>();
        if (!assignedLessons.isEmpty()){
            // this allows items to be added via List.Of()/Arrays.asList()
            this.assignedLessons.addAll(assignedLessons);
        }
    }

    public Coach(String name) {
        this(name, new ArrayList<>());
    }

    public String getName() {
        return name;
    }

    public List<Lesson> getAssignedLessons() {
        return assignedLessons;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void assignLesson(Lesson lesson) {
        assignedLessons.add(lesson);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coach coach = (Coach) o;
        return Objects.equals(name, coach.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Coach{" +
                "name='" + name + '\'' +
                ", assignedLessons=" + assignedLessons +
                '}';
    }
}
