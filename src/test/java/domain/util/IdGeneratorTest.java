package domain.util;

import domain.entity.coach.Coach;
import domain.entity.learner.Learner;
import domain.entity.lesson.Lesson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class IdGeneratorTest {
    @Test
    void generateId_forLearner_idIsSixDigits() {
        Learner learner = new Learner("Divine", "Male", 5, 0, "08172742510", "08172432510");

        String id = String.valueOf(IdGenerator.generateId(learner));

        assertEquals(6, id.length());
    }

    @Test
    void generateId_forLearner_idStartsWithTwo() {
        Learner learner = new Learner("Divine", "Male", 5, 0, "08172742510", "08172432510");

        String id = String.valueOf(IdGenerator.generateId(learner));

        assertTrue(id.startsWith("2"));
    }

    @Test
    void generateId_forLesson_idIsSixDigits() {
        Lesson lesson = new Lesson("test", 5, new Coach("paul"), LocalDate.now(), "08172432510");

        String id = String.valueOf(IdGenerator.generateId(lesson));

        assertEquals(6, id.length());
    }

    @Test
    void generateId_forLesson_idStartsWithOne() {
        Lesson lesson = new Lesson("test", 5, new Coach("paul"), LocalDate.now(), "08172432510");

        String id = String.valueOf(IdGenerator.generateId(lesson));

        assertTrue(id.startsWith("1"));
    }

    @Test
    void testGenerateId() {
    }
}