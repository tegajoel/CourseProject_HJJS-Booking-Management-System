package domain.util;

import domain.entity.learner.Learner;
import domain.entity.lesson.Lesson;

import java.util.Random;

/**
 * ID generator
 */
public class IdGenerator {
    private static final Random random = new Random();
    private static final String LESSON_PREFIX = "1";  // First digit for lesson IDs
    private static final String LEARNER_PREFIX = "2"; // First digit for learner IDs
    private static final int ID_RANGE = 1000;    // Range for the remaining four digits

    /**
     * Generate a new id for this learner
     *
     * @param learner the learner
     * @return generated id
     */
    public static int generateId(Learner learner) {
        String id = LEARNER_PREFIX + learner.getGrade() + String.format("%04d", random.nextInt(ID_RANGE));
        return Integer.parseInt(id);
    }

    /**
     * Generate a new id for this lesson
     *
     * @param lesson the lesson
     * @return generated id
     */
    public static int generateId(Lesson lesson) {
        String id = LESSON_PREFIX + lesson.getGrade() + String.format("%04d", random.nextInt(ID_RANGE));
        return Integer.parseInt(id);
    }

}