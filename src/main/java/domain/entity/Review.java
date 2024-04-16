package domain.entity;

public class Review {
    private String message;
    private int rating;

    public static final Review NONE = new Review("", -1);

    public Review(String message, int rating) {
        this.message = message;
        this.rating = rating;
    }

    public String getMessage() {
        return message;
    }

    public void updateMessage(String message) {
        this.message = message;
    }

    public int getRating() {
        return rating;
    }

    public void updateRating(int rating) {
        this.rating = rating;
    }
}
