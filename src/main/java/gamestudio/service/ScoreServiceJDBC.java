package gamestudio.service;

import gamestudio.entity.Score;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class ScoreServiceJDBC implements ScoreService {
    private static final String CREATE_STATEMENT = "CREATE TABLE score (game VARCHAR(64) NOT NULL" +
            ", player VARCHAR(64) NOT NULL, points INTEGER  NOT NULL , played_at TIMESTAMP WITHOUT TIME ZONE NOT NULL )";
    private static final String SELECT_STATEMENT = "SELECT game, player, points, played_at FROM score WHERE game = ? ORDER BY points DESC LIMIT 10";
    private static final String DELETE_STATEMENT = "DELETE FROM score";
    private static final String INSERT_STATEMENT = "INSERT INTO score(game, player, points, played_at) VALUES (?, ?, ?, ?)";

    private DataSource dataSource;

    public ScoreServiceJDBC(DataSource dataSource){
        this.dataSource = dataSource;
    }

    @Override
    public void addScore(Score score) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_STATEMENT)
        ) {
            statement.setString(1, score.getGame());
            statement.setString(2, score.getPlayer());
            statement.setInt(3, score.getPoints());
            statement.setTimestamp(4, new Timestamp(score.getPlayedOn().getTime()));
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new ScoreException("Problem inserting score", e);
        }
    }

    @Override
    public List<Score> getTopScores(String game) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_STATEMENT);
        ) {
            statement.setString(1, game);
            try (ResultSet rs = statement.executeQuery()) {
                List<Score> scores = new ArrayList<>();
                while (rs.next()) {
                    scores.add(new Score(rs.getString(1), rs.getString(2), rs.getInt(3), rs.getTimestamp(4)));
                }
                return scores;
            }
        } catch (SQLException e) {
            throw new ScoreException("Problem selecting score", e);
        }
    }

    @Override
    public void reset() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
        ) {
            statement.executeUpdate(DELETE_STATEMENT);
        } catch (SQLException e) {
            throw new ScoreException("Problem deleting score", e);
        }
    }
}
