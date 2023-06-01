package gamestudio.entity;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import java.util.Date;

@Entity
@NamedQuery( name = "Rating.resetRating",
        query = "DELETE FROM Rating")
@NamedQuery( name = "Rating.getAverageRating",
        query = "SELECT AVG(r.rating) FROM Rating r WHERE r.game = :game")
@NamedQuery( name = "Rating.updateRating",
        query = "UPDATE Rating r SET r.rating = :newRating, r.RatedOn = :ratedOn WHERE r.game = :game AND r.player = :player")
@NamedQuery( name = "Rating.getRating",
        query = "SELECT r.rating FROM Rating r WHERE r.game = :game AND r.player = :player")
public class Rating {
    private String game;

    private String player;

    private int rating;

    private Date RatedOn;
    @Id
    @GeneratedValue
    private int ident;
    public Rating(){};

    public Rating(String game, String player, int rating, Date RatedOn) {
        this.game = game;
        this.player = player;
        this.rating = rating;
        this.RatedOn = RatedOn;
    }

    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public Date getRatedOn() {
        return RatedOn;
    }

    public void setRatedOn(Date ratedOn) {
        this.RatedOn = ratedOn;
    }

    @Override
    public String toString() {
        return "Rating{" +
                "game='" + game + '\'' +
                ", player='" + player + '\'' +
                ", Rating=" + rating +
                ", RatedOn=" + RatedOn +
                '}';
    }

    public void setIdent(int ident) {
        this.ident = ident;
    }

    public int getIdent() {
        return ident;
    }
}
