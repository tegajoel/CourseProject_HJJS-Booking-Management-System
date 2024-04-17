package domain.util;

import domain.entity.Coach;
import domain.entity.lesson.Lesson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LessonUtilTest {

    @Test
    void getCoachesFromLessons_returnsSetWithAllCoaches() {
        Coach coach1 = new Coach("Helen Paul");
        Coach coach2 = new Coach("Sam Johnson");
        Coach coach3 = new Coach("John Smith");
        Lesson lesson1 = new Lesson("Diving1", 3, coach1, LocalDate.now());
        Lesson lesson3 = new Lesson("Diving3", 2, coach2, LocalDate.now());
        Lesson lesson2 = new Lesson("Diving2", 3, coach2, LocalDate.now());
        Lesson lesson4 = new Lesson("Diving4", 1, coach2, LocalDate.now());

        List<Lesson> lessons = Arrays.asList(lesson1, lesson2, lesson3, lesson4);

        var result = LessonUtil.getCoachesFromLessons(lessons);

        assertTrue(result.contains(coach1));
        assertTrue(result.contains(coach2));
        assertFalse(result.contains(coach3));
    }

    @Test
    void getCoachesFromLessons_returnsSetWithExpectedSize() {
        Coach coach1 = new Coach("Helen Paul");
        Coach coach2 = new Coach("Sam Johnson");
        Lesson lesson1 = new Lesson("Diving1", 3, coach1, LocalDate.now());
        Lesson lesson3 = new Lesson("Diving3", 2, coach2, LocalDate.now());
        Lesson lesson2 = new Lesson("Diving2", 3, coach2, LocalDate.now());
        Lesson lesson4 = new Lesson("Diving4", 1, coach2, LocalDate.now());

        List<Lesson> lessons = Arrays.asList(lesson1, lesson2, lesson3, lesson4);

        var result = LessonUtil.getCoachesFromLessons(lessons);

        assertEquals(2, result.size());
    }
}