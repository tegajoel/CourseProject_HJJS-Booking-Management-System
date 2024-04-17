package domain.usecase;

import domain.entity.lesson.Lesson;
import domain.repository.LessonRepository;
import domain.util.Result;

import java.util.ArrayList;
import java.util.List;

public class FilterLessonsUseCase {
    private final LessonRepository lessonRepository;

    public FilterLessonsUseCase(LessonRepository lessonRepository) {
        this.lessonRepository = lessonRepository;
    }

    public Result<List<Lesson>, Error> filterByGrade(int grade){
        return Result.success(new ArrayList<>());
    }


    public Result<List<Lesson>, Object> filterByCoach(String coachName){
        return Result.success(new ArrayList<>());
    }

    public Result<List<Lesson>, Error> filterByDay(String weekDay){
        return Result.success(new ArrayList<>());
    }


    public enum Error {
        INVALID_DAY, INVALID_GRADE
    }

}
