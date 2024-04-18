package data;

import domain.entity.lesson.Lesson;
import domain.repository.LessonRepository;

import java.util.ArrayList;
import java.util.List;

public class InMemLessonRepository implements LessonRepository {
    private final List<Lesson> lessons = new ArrayList<>();

    private static InMemLessonRepository INSTANCE = null;

    public static InMemLessonRepository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new InMemLessonRepository();
        }
        return INSTANCE;
    }

    private InMemLessonRepository(){

    }

    @Override
    public List<Lesson> getAllLessons() {
        return lessons;
    }

    @Override
    public void addNewLesson(Lesson lesson) {
        lessons.add(lesson);
    }

    public void addLessons(List<Lesson> lessons) {
        this.lessons.addAll(lessons);
    }
}
