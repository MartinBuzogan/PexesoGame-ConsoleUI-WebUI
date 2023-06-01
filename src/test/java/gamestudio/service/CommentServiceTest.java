package gamestudio.service;

import gamestudio.entity.Comment;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
@SpringBootTest
public class CommentServiceTest {
    @Autowired
    private CommentService commentService;

    @Test
    public void reset() {
        commentService.reset();
        assertEquals(0, commentService.getComments("pexeso").size());
    }

    @Test
    public void addComment() {
        commentService.reset();
        var date = new Date();

        commentService.addComment(new Comment("pexeso", "Martin", "Great game", date));

        var scores = commentService.getComments("pexeso");
        assertEquals(1, scores.size());
        assertEquals("pexeso", scores.get(0).getGame());
        assertEquals("Martin", scores.get(0).getPlayer());
        assertEquals("Great game", scores.get(0).getComment());
        assertEquals(date, scores.get(0).getCommentedOn());
    }
}
