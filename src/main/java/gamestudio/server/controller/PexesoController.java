package gamestudio.server.controller;

import gamestudio.entity.Comment;
import gamestudio.entity.Rating;
import gamestudio.entity.Score;
import gamestudio.pexeso.core.Field;
import gamestudio.pexeso.core.FieldState;
import gamestudio.pexeso.core.TileState;
import gamestudio.service.CommentService;
import gamestudio.service.RatingService;
import gamestudio.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.net.URISyntaxException;

import java.util.Date;
import java.util.concurrent.TimeUnit;


@Controller
@RequestMapping("/pexeso")
@Scope(WebApplicationContext.SCOPE_SESSION)
public class PexesoController {
    private int row = 2;
    private int col = 2;
    @Autowired
    private ScoreService scoreService;
    @Autowired
    private RatingService ratingService;
    @Autowired
    private CommentService commentService;

    private Field field;
    {
        try {
            field = new Field(row, col);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping
    public String pexeso(@RequestParam(required = false) Integer row, @RequestParam(required = false) Integer column) {
        if(row != null && column != null)
            field.RevealTile(getIndexFromInput(row,column));
        return "pexeso";
    }
    @RequestMapping(value = "/field", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String pexesoField(@RequestParam(required = false) Integer row, @RequestParam(required = false) Integer column) {
        if(row != null && column != null)
            field.RevealTile(getIndexFromInput(row,column));
        return getHtmlField();
    }


    public String getHtmlField() {
        var sb = new StringBuilder();
        sb.append("<table class='pexesofield'>\n");
        for (var row = 0; row < field.getRowCount(); row++) {
            sb.append("<tr>\n");
            for (var column = 0; column < field.getColumnCount(); column++) {
                var tile = field.getTile(row*field.getRowCount()+column);
                sb.append("<td class='" + tile.getState() + "'>\n");
                if (tile.getState() == TileState.HIDDEN) {
                    sb.append("<a onclick='refreshPexesofield(\"/pexeso/field?row=" + row + "&column=" + column + "\")'>\n");
                    sb.append("<span> don't cheat </span>\n");
                    sb.append("</a>\n");
                } else {
                    sb.append("<a onclick='refreshPexesofield(\"/pexeso/field?row=" + row + "&column=" + column + "\")'>\n");
                    sb.append("<img src='" + tile.getImgPath() + "' alt='" + tile.getValue() + "'>\n");
                    sb.append("</a>\n");
                }
                sb.append("</td>\n");
            }
            sb.append("</tr>\n");
        }
        sb.append("</table>\n");
        return sb.toString();
    }
    @GetMapping("/new")
    public String newGame() throws IOException, URISyntaxException {
        field = new Field(row, col);
        return "pexeso";
    }
    @RequestMapping(value = "/gamestate", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String gameState() throws InterruptedException, IOException, URISyntaxException {
        boolean isSolved = field.getFieldState() == FieldState.SOLVED;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if( isSolved){
            StringBuilder sb = new StringBuilder();
            sb.append("<div id=\"loginCard\" class=\"card login\">");
            sb.append("<h3>You Won!</h3>");
            sb.append("</div>");

            if(!(authentication instanceof AnonymousAuthenticationToken)){
                scoreService.addScore(new Score("pexeso",authentication.getName(), field.getScore(), new Date()));
                TimeUnit.SECONDS.sleep(1);
                newGame();
            }
            return sb.toString();
        }
        return "";
    }
    private int getIndexFromInput(int row, int column) {
        return ((row * field.getColumnCount()) + column);
    }
    @RequestMapping(value = "/rate", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<String> submitRating(@RequestBody Rating rating) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if((authentication instanceof AnonymousAuthenticationToken))
            return ResponseEntity.ok("Not logged user");
        rating.setRatedOn(new Date());
        rating.setGame("pexeso");
        rating.setPlayer(authentication.getName());
        ratingService.setRating(rating);
        return ResponseEntity.ok("Rating submitted successfully");
    }
    @RequestMapping(value = "/comment", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<String> submitComment(@RequestBody Comment comment) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if((authentication instanceof AnonymousAuthenticationToken))
            return ResponseEntity.ok("Not logged user");
        comment.setCommentedOn(new Date());
        comment.setGame("pexeso");
        comment.setPlayer(authentication.getName());
        commentService.addComment(comment);
        return ResponseEntity.ok("comment submitted successfully");
    }
    @RequestMapping(value = "/changedfield", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> gameState(@RequestParam(required = false) Integer row, @RequestParam(required = false) Integer column) throws IOException, URISyntaxException {
        if(row != null && column != null) {
            this.row = row;
            this.col = column;
            field = new Field(row, column);
        }
        return ResponseEntity.ok("Field changed");
    }

}
