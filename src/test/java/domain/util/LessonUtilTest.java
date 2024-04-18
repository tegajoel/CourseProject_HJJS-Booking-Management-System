package domain.util;

import domain.entity.Coach;
import domain.entity.Rating;
import domain.entity.Review;
import domain.entity.lesson.Lesson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

    @Test
    void getAverageReviewRating_emptyReviewList_returnsNoRating(){
        Rating rating = LessonUtil.getAverageReviewRating(new ArrayList<>());

        assertEquals(Rating.NONE, rating);
    }

    @Test
    void getAverageReviewRating_reviewListOneItem_returnsItemRating(){

        Rating rating = LessonUtil.getAverageReviewRating(List.of(new Review("Good", 4)));

        assertEquals(4, rating.getRatingValue());
    }

    @Test
    void getAverageReviewRating_reviewListMultipleItems_returnsAverageItemRating(){
        var reviews = List.of(new Review("Good", 4), new Review("Great", 5), new Review("Meh", 2));
        Rating rating = LessonUtil.getAverageReviewRating(reviews);

        assertEquals(3.7, rating.getRatingValue());
    }

    @Test
    void getAverageReviewRating_ratingsRoundToOneDecimalPlace(){
        var reviews = List.of(new Review("Good", 4), new Review("Great", 5), new Review("Meh", 2));
        Rating rating = LessonUtil.getAverageReviewRating(reviews);

        assertTrue(hasDecimalPlaces(rating.getRatingValue(),1));
    }

    /**
     * Tests to see whether the number has up to the given number of
     * decimal places.
     *
     * @param value The value to test.
     * @param scale The maximum number of decimal places.
     * @return <code>true</code> if the value has up to the
     * expected number of decimal places.
     */
    private static boolean hasDecimalPlaces(
            final double value,
            final int scale
    ) {
        try {
            new BigDecimal(Double.toString(value)).setScale(scale, RoundingMode.HALF_UP);
            return true;
        } catch (final ArithmeticException ex) {
            return false;
        }
    }
}