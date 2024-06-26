package domain.repository;

import domain.entity.lesson.Lesson;

import java.util.List;

public interface LessonRepository {
    /**
     * Get all the list of lessons in the repository
     * @return the list of lessons
     */
    List<Lesson> getAllLessons();

    /**
     * Add a new lesson to the repository
     * @param lesson lesson to be added
     */
    void addNewLesson(Lesson lesson);
}
