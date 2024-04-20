package domain.usecase;

import domain.entity.coach.Coach;
import domain.entity.lesson.Lesson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BuildTimetableUseCase {
    // [water safety, Backstroke training, Sidestroke training, Freestyle training, Butterfly Technique, Open Water Endurance, Diving Fundamentals]
    private List<String> preLessonNames = List.of(
            "Water Safety",
            "Backstroke Training",
            "Sidestroke Training",
            "Freestyle Training",
            "Butterfly Technique",
            "Breaststroke Technique",
            "Open Water Endurance",
            "Diving Fundamentals",
            "Synchronized Swimming"
    );

    /**
     * Generate a list of lessons
     * @param coaches coaches to teach the lesson
     * @return a list of generated lessons with a coach assigned to each of them
     */
    public List<Lesson> buildAndGetLessons(List<Coach> coaches){
        if (coaches.isEmpty()) throw new IllegalArgumentException("The list of coaches must not be empty. There should be at lease one coach");

        List<String> lessonNames = generateLessonNames(preLessonNames);

        Collections.shuffle(lessonNames); // shuffle the list to randomize it





        List<Lesson> lessons = new ArrayList<>();
//        lessons.add(new Lesson())
        return new ArrayList<>();
    }

    private List<String> generateLessonNames(List<String> preLessonNames){
        List<String> result = new ArrayList<>();

        for (String preLessonName : preLessonNames) {
            for (int i = 1; i <= 5; i++) {
                result.add(preLessonName + " " + i);
            }
        }
        return result;
    }
}
