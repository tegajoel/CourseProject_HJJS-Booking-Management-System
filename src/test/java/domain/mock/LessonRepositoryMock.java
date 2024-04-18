package domain.mock;

import domain.entity.lesson.Lesson;
import domain.repository.LessonRepository;

import java.util.ArrayList;
import java.util.List;

public class LessonRepositoryMock implements LessonRepository {
    private final List<Lesson> lessons = new ArrayList<>();

    @Override
    public List<Lesson> getAllLessons() {
        return lessons;
    }

    @Override
    public void addNewLesson(Lesson lesson) {
        lessons.add(lesson);
    }
}
