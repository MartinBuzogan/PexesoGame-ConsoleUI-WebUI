package gamestudio;

import gamestudio.pexeso.core.Field;
import gamestudio.service.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import gamestudio.pexeso.ConsoleUI.ConsoleUI;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.FilterType;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.URISyntaxException;

@SpringBootApplication
@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX,
        pattern = "gamestudio.server.*"))
public class SpringClient {
    public static void main(String[] args) {
        new SpringApplicationBuilder(SpringClient.class).web(WebApplicationType.NONE).run(args);    }

    @Bean
    public RatingService ratingService(DataSource dataSource) {
        return new RatingServiceRestClient();
    }

    @Bean
    public CommentService commentService(DataSource dataSource) {
        return new CommentServiceRestClient();
    }
    @Bean
    public ScoreService scoreService(DataSource dataSource) {
        return new ScoreServiceRestClient();
    }

    @Bean
    @DependsOn({"scoreService", "commentService","ratingService"})
    public CommandLineRunner runner(ConsoleUI consoleUI) {
        return s -> consoleUI.play();
    }
    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

    @Bean
    public Field field() throws IOException, URISyntaxException {
        return new Field(6, 6);
    }
    @Bean
    public ConsoleUI consoleUI(Field field) {
        return new ConsoleUI(field);
    }
}
