package domain.util;

import domain.entity.Learner;
import domain.entity.lesson.Lesson;

import java.util.Random;

/**
 * ID generator
 */
public class IdGenerator {
    private static final Random random = new Random();
    private static final int LESSON_PREFIX = 1;  // First digit for lesson IDs
    private static final int LEARNER_PREFIX = 2; // First digit for learner IDs
    private static final int ID_RANGE = 100;    // Range for the remaining three digits

    /**
     * Generate a new id for this learner
     *
     * @param learner the learner
     * @return generated id
     */
    public static int generateId(Learner learner) {
        return Integer.parseInt(LEARNER_PREFIX + learner.getGrade() + String.format("%03d", random.nextInt(ID_RANGE)));
    }

    /**
     * Generate a new id for this lesson
     *
     * @param lesson the lesson
     * @return generated id
     */
    public static int generateId(Lesson lesson) {
        return Integer.parseInt(LESSON_PREFIX + lesson.getGrade() + String.format("%03d", random.nextInt(ID_RANGE)));
    }

}