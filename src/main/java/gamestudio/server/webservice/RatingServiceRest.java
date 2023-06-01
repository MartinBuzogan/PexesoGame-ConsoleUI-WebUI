package gamestudio.server.webservice;
import gamestudio.entity.Rating;
import gamestudio.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rating")
public class RatingServiceRest {

    @Autowired
    private RatingService ratingService;

    @PostMapping
    public void setRating(@RequestBody Rating rating){
        ratingService.setRating(rating);
    }
    @GetMapping("/avg/{game}")
    public int getAverageRating(@PathVariable String game){
        return ratingService.getAverageRating(game);
    }
    @GetMapping("/{game}/{player}")
    public int getRating(@PathVariable String game ,@PathVariable String player){
        return ratingService.getRating(game,player);
    }
}
