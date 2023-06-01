package gamestudio.service;

import gamestudio.entity.Rating;

import javax.sql.DataSource;
import java.sql.*;

public class RatingServiceJDBC implements RatingService {
    private static final String CREATE_STATEMENT = "CREATE TABLE rating (game VARCHAR(64) NOT NULL" +
            ", player VARCHAR(64) NOT NULL, rating INTEGER  NOT NULL , rated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL )";
    public static final String SELECT_AVERAGE = "SELECT game, player, rating, rated_at FROM rating WHERE game = ?";
    public static final String SELECT_PLAYER = "UPDATE rating SET rating = ?, rated_at = ? WHERE game = ? AND player = ?";
    public static final String SELECT_PLAYER2 = "SELECT game, player, rating, rated_at FROM rating WHERE game = ? and player =?";
    public static final String DELETE = "DELETE FROM rating";
    public static final String INSERT_STATEMENT = "INSERT INTO rating (game, player, rating, rated_at) VALUES (?, ?, ?, ?)";
    private DataSource dataSource;

    public RatingServiceJDBC(DataSource dataSource){
        this.dataSource = dataSource;
    }

    @Override
    public void setRating(Rating rating) throws RatingException {
        if(getRating(rating.getGame(), rating.getPlayer()) == -1){
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement(INSERT_STATEMENT)
            ) {
                statement.setString(1, rating.getGame());
                statement.setString(2, rating.getPlayer());
                statement.setInt(3, rating.getRating());
                statement.setTimestamp(4, new Timestamp(rating.getRatedOn().getTime()));
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new ScoreException("Problem inserting score", e);
            }
        }else {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement(SELECT_PLAYER);
            ) {
                statement.setString(3, rating.getGame());
                statement.setString(4, rating.getPlayer());
                statement.setInt(1, rating.getRating());
                statement.setTimestamp(2, new Timestamp(rating.getRatedOn().getTime()));
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RatingException("Problem set rating", e);
            }
        }
    }

    @Override
    public int getAverageRating(String game) throws RatingException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_AVERAGE);
        ) {
            statement.setString(1, game);
            try (ResultSet rs = statement.executeQuery()) {
                int rating = 0;
                int numberOfrating = 0;
                while (rs.next()) {
                    rating += rs.getInt(3);
                    numberOfrating++;
                }
                if(numberOfrating == 0)
                    return 0;
                return Math.round(rating / numberOfrating);
            }
        } catch (SQLException e) {
            throw new RatingException("Problem selecting rating", e);
        }
    }

    @Override
    public int getRating(String game, String player) throws RatingException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_PLAYER2);
        ) {
            statement.setString(1, game);
            statement.setString(2, player);
            try (ResultSet rs = statement.executeQuery()){
                if(rs.next())
                    return rs.getInt(3);
                else
                    return -1;
            }
        } catch (SQLException e) {
            throw new RatingException("Problem selecting comments", e);
        }
    }

    @Override
    public void reset() throws RatingException {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
        ) {
            statement.executeUpdate(DELETE);
        } catch (SQLException e) {
            throw new RatingException("Problem deleting rating", e);
        }
    }
}

