package gamestudio.service;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import gamestudio.entity.Score;


import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ScoreServiceTest {
    @Autowired
    private ScoreService scoreService;

    @Test
    public void reset() {
        scoreService.reset();
        assertEquals(0, scoreService.getTopScores("pexeso").size());
    }

    @Test
    public void addScore() {
        scoreService.reset();
        var date = new Date();

        scoreService.addScore(new Score("pexeso", "Martin", 100, date));

        var scores = scoreService.getTopScores("pexeso");
        assertEquals(1, scores.size());
        assertEquals("pexeso", scores.get(0).getGame());
        assertEquals("Martin", scores.get(0).getPlayer());
        assertEquals(100, scores.get(0).getPoints());
        assertEquals(date, scores.get(0).getPlayedOn());
    }

    @Test
    public void getTopScores() {
        scoreService.reset();
        var date = new Date();
        scoreService.addScore(new Score("pexeso", "Alojz", 120, date));
        scoreService.addScore(new Score("pexeso", "Adam", 150, date));
        scoreService.addScore(new Score("mines", "Marek", 90, date));
        scoreService.addScore(new Score("pexeso", "Vilo", 100, date));

        var scores = scoreService.getTopScores("pexeso");

        assertEquals(3, scores.size());

        assertEquals("pexeso", scores.get(0).getGame());
        assertEquals("Adam", scores.get(0).getPlayer());
        assertEquals(150, scores.get(0).getPoints());
        assertEquals(date, scores.get(0).getPlayedOn());

        assertEquals("pexeso", scores.get(1).getGame());
        assertEquals("Alojz", scores.get(1).getPlayer());
        assertEquals(120, scores.get(1).getPoints());
        assertEquals(date, scores.get(1).getPlayedOn());

        assertEquals("pexeso", scores.get(2).getGame());
        assertEquals("Vilo", scores.get(2).getPlayer());
        assertEquals(100, scores.get(2).getPoints());
        assertEquals(date, scores.get(2).getPlayedOn());
    }
}
