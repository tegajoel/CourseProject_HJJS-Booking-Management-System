package domain.usecase;

import domain.entity.lesson.Lesson;
import domain.repository.LessonRepository;
import domain.util.Result;

import java.time.DayOfWeek;
import java.util.List;

public class FilterLessonsUseCase {
    private final LessonRepository lessonRepository;

    public FilterLessonsUseCase(LessonRepository lessonRepository) {
        this.lessonRepository = lessonRepository;
    }

    /**
     * Get a filtered the list of lessons by their grade
     *
     * @param grade grade
     * @return result with {@link List<Lesson>} on success, and {@link Error} on failure
     */
    public Result<List<Lesson>, Error> filterByGrade(int grade) {
        if (grade < 0) {
            return Result.error(Error.INVALID_INPUT);
        }
        return Result.success(lessonRepository.getAllLessons().stream().filter(lesson -> lesson.getGrade() == grade).toList());
    }


    /**
     * Get a filtered the list of lessons by the coach
     *
     * @param coachName name of the coach
     * @return result with {@link List<Lesson>} on success, and {@link Error} on failure
     */
    public Result<List<Lesson>, Object> filterByCoach(String coachName) {
        if (coachName == null || coachName.isBlank()) {
            return Result.error(Error.INVALID_INPUT);
        }

        return Result.success(lessonRepository.getAllLessons()
                .stream()
                .filter(lesson -> lesson.getCoach().getName().equalsIgnoreCase(coachName.trim()))
                .toList()
        );

    }

    /**
     * Get a filtered the list of lessons by the week day
     *
     * @param weekDay day of the week
     * @return result with {@link List<Lesson>} on success, and {@link Error} on failure
     */
    public Result<List<Lesson>, Error> filterByDay(String weekDay) {
        if (weekDay == null || weekDay.isBlank()) {
            return Result.error(Error.INVALID_INPUT);
        }

        try {
            DayOfWeek dayOfWeek = DayOfWeek.valueOf(weekDay.trim().toUpperCase());
            return Result.success(lessonRepository.getAllLessons()
                    .stream()
                    .filter(lesson -> lesson.getLessonDate().getDayOfWeek() == dayOfWeek)
                    .toList()
            );
        } catch (Exception e) {
            return Result.error(Error.INVALID_INPUT);
        }
    }


    public enum Error {
        INVALID_INPUT
    }
}
