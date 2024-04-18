package domain.entity;

import java.util.Objects;

/**
 * This wrapper data class holds a rating
 * <p>
 * It's useful where in situations where there is no initial rating
 */
public class Rating {

    private Double ratingValue; // Use Integer to allow for null (no rating)

    public static Rating NONE = new Rating();

    public Rating() {
        // Constructor for no rating
        this.ratingValue = null;
    }

    public Rating(double ratingValue) {
        // Constructor for existing rating
        this.ratingValue = ratingValue;
    }

    public boolean hasRating() {
        // Check if a rating exists
        return ratingValue != null;
    }

    public double getRatingValue() {
        // Get the rating value, assuming it exists
        if (hasRating()) {
            return ratingValue;
        } else {
            throw new IllegalStateException("No rating present");
        }
    }

    /**
     * Set a new rating value
     *
     * @param ratingValue
     */
    public void setRatingValue(double ratingValue) {
        this.ratingValue = ratingValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rating rating = (Rating) o;
        return Objects.equals(ratingValue, rating.ratingValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ratingValue);
    }
}
