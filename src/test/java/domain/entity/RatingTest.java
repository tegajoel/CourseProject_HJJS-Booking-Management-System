package domain.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RatingTest {

    private Rating SUT;
    @BeforeEach
    void setUp() {
        SUT = new Rating();
    }

    @Test
    public void setRatingValue_returnsRatingWhenRequested(){
        SUT.setRatingValue(4.0);
        assertEquals(4.0, SUT.getRatingValue());
    }

    @Test
    public void setRatingValueViaConstructor_returnsRatingWhenRequested(){
        Rating rating = new Rating(5);
        assertEquals(5, rating.getRatingValue());
    }

    @Test
    public void hasRating_ratingSet_returnsTrue(){
        SUT.setRatingValue(4.0);
        assertTrue(SUT.hasRating());
    }

    @Test
    public void hasRating_ratingSetViaConstructor_returnsTrue(){
        Rating rating = new Rating(5);
        assertTrue(rating.hasRating());
    }

    @Test
    public void hasRating_ratingNotSet_returnsFalse(){
        assertFalse(SUT.hasRating());
    }

    @Test
    public void getRatingValue_ratingNotSet_throwsException(){
        assertThrows(IllegalStateException.class, () -> SUT.getRatingValue());
    }
}