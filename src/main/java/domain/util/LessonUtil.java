package domain.util;

import domain.entity.Coach;
import domain.entity.lesson.Lesson;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LessonUtil {

    /**
     * Get the unique set of coached from the list of lessons
     * @param lessons lessons
     * @return a unique set of coaches
     */
    public static Set<Coach> getCoachesFromLessons(List<Lesson> lessons){
        var result = new HashSet<Coach>();
        for (Lesson lesson : lessons) {
            result.add(lesson.getCoach());
        }
        return result;
    }
}
