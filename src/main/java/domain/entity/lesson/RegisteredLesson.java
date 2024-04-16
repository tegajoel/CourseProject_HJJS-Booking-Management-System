package domain.entity.lesson;

public class RegisteredLesson {
    private final Lesson lesson;
    private LessonStatus lessonStatus;


    public RegisteredLesson(Lesson lesson, LessonStatus lessonStatus) {
        this.lesson = lesson;
        this.lessonStatus = lessonStatus;
    }

    public Lesson getLesson() {
        return lesson;
    }

    public LessonStatus getLessonStatus() {
        return lessonStatus;
    }

    public void setLessonStatus(LessonStatus lessonStatus) {
        this.lessonStatus = lessonStatus;
    }
}
