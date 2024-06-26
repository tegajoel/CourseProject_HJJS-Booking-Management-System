package domain.util;

import domain.entity.coach.Coach;
import domain.entity.Rating;
import domain.entity.Review;
import domain.entity.lesson.Lesson;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LessonUtil {

    /**
     * Get the unique set of coached from the list of lessons
     *
     * @param lessons lessons
     * @return a unique set of coaches
     */
    public static Set<Coach> getCoachesFromLessons(List<Lesson> lessons) {
        var result = new HashSet<Coach>();
        for (Lesson lesson : lessons) {
            result.add(lesson.getCoach());
        }
        return result;
    }

    /**
     * Get the average rating from reviews
     *
     * @param review list of reviews
     * @return the average rating value if there is at least one rating, {@link Rating#NONE} otherwise
     */
    public static Rating getAverageReviewRating(List<Review> review) {
        if (review.isEmpty()) return Rating.NONE;

        double initialRating = 0;
        for (Review review1 : review) {
            initialRating += review1.getRating();
        }

        double roundedAverage = new BigDecimal(initialRating / review.size()).setScale(1, RoundingMode.HALF_UP).doubleValue();
        return new Rating(roundedAverage);
    }

    /**
     * Get the average Rating from list of ratings
     *
     * @param ratings list of ratings
     * @return the average rating value if there is at least one valid rating, {@link Rating#NONE} otherwise
     */
    public static Rating getAverageRating(List<Rating> ratings) {
        if (ratings.isEmpty()) return Rating.NONE;


        var filteredRatings = ratings.stream().filter(Rating::hasRating).toList();
        if (filteredRatings.isEmpty()) return Rating.NONE;

        double initialRating = 0;

        for (Rating rating : filteredRatings) {
            initialRating += rating.getRatingValue();
        }

        double roundedAverage = new BigDecimal(initialRating / filteredRatings.size()).setScale(1, RoundingMode.HALF_UP).doubleValue();
        return new Rating(roundedAverage);
    }
}
