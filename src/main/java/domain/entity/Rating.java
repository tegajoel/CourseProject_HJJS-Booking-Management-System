package domain.entity;

/**
 * This wrapper data class holds a rating
 * <p>
 * It's useful where in situations where there is no initial rating
 */
public class Rating {

    private Double ratingValue; // Use Integer to allow for null (no rating)

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
}
