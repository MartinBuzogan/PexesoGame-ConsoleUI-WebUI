package gamestudio.service;

import gamestudio.entity.Rating;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
@SpringBootTest
public class RatingServiceTest {
    @Autowired
    private RatingService ratingService;

    @Test
    public void reset() {
        ratingService.reset();
        assertEquals(0, ratingService.getAverageRating("pexeso"));
    }
    @Test
    public void setRating() {
        ratingService.reset();
        var date = new Date();
        ratingService.setRating(new Rating("pexeso","Alfonz",2,date));
        ratingService.setRating(new Rating("pexeso","Alfonz",5,date));
        var rating = ratingService.getRating("pexeso","Alfonz");
        assertEquals(5, rating);
    }
    @Test
    public void getAverageRating(){
        ratingService.reset();
        var date = new Date();
        ratingService.setRating(new Rating("pexeso","Alfonz",4,date));
        ratingService.setRating(new Rating("pexeso","Alfonz",5,date));
        ratingService.setRating(new Rating("pexeso","Adam",3,date));
        ratingService.setRating(new Rating("pexeso","Marek",2,date));
        var rating = ratingService.getAverageRating("pexeso");
        assertEquals(3, rating);
    }
}
