package gamestudio.pexeso.ConsoleUI;

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

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLOutput;
import java.util.Date;
import java.util.Scanner;
import java.util.regex.Pattern;


public class ConsoleUI {
    private static final String ANSI_RED = "\033[0;91m";
    private static final String ANSI_GREEN = "\033[0;92m";

    private static final String ANSI_GOLD = "\033[0;93m";
    private static final String ANSI_BLUE = "\033[0;94m";
    private static final String ANSI_CYAN = "\033[0;96m";
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String WHITE_BRIGHT = "\033[0;97m";
    private static final String WHITE_bold_BRIGHT = "\033[1;97m";
    private Field gameField;
    private final String Hidden_Tiles = "\uD83D\uDD32";
    private final String Hidden_Tiles1 = "\uD83D\uDD33";
    private final String Logo = ANSI_GREEN + "        _ __   _____  _____  ___  ___  \n" + ANSI_RED + "       | '_ \\ / _ \\ \\/ / _ \\/ __|/ _ \\ \n" + ANSI_GOLD + "       | |_) |  __/>  <  __/\\__ \\ (_) |\n" + ANSI_CYAN + "       | .__/ \\___/_/\\_\\___||___/\\___/ \n" + ANSI_BLUE + "       | |                             \n" + "       |_|                             " + ANSI_RESET;
    private final String underline = "    ______________________________________";
    private static final String gameName = "pexeso";
    private Scanner scanner;
    private Pattern PositionPattern;
    private static Pattern FieldSizePattern = Pattern.compile("[1-9]");
    private static Pattern MenuInputPattern = Pattern.compile("[1-3]");
    private ScoreService scoreService;
    private RatingService ratingService;
    private CommentService commentService;
    private String UserName;
    public ConsoleUI(Field field) {
        this.gameField = field;
        this.scanner = new Scanner(System.in);
        calculateFiedlInputRegex();
    }
    @Autowired
    public void setScoreService(ScoreService scoreService) {
        this.scoreService = scoreService;
    }
    @Autowired
    public void setRatingService( RatingService ratingService) {
        this.ratingService = ratingService;
    }
    @Autowired
    public void setCommentService( CommentService commentService) {
        this.commentService = commentService;
    }

    private void calculateFiedlInputRegex() {
        PositionPattern = Pattern.compile("X|([A-" + (char) (65 + gameField.getRowCount() - 1) +
                "])-([1-" + (gameField.getColumnCount() + "])"));
    }

    private int gameMenuInput() {
        displayMenu();
        return numberInput(MenuInputPattern,"number");
    }

    private int numberInput(Pattern regexPattern,String command) {
        String inputIntro;
        boolean correctInput = false;
        do {
            System.out.print("Enter "+command+": ");
            inputIntro = scanner.nextLine();
            if (regexPattern.matcher(inputIntro).matches()) {
                correctInput = true;
                break;
            }
            System.out.println(ANSI_RED+"Wrong input."+ANSI_RESET);
        } while (!correctInput);
        return Integer.parseInt(inputIntro);
    }

    private void displayMenu() {
        System.out.println(WHITE_bold_BRIGHT + underline + ANSI_RESET);
        System.out.println(Logo);
        System.out.println("\n" + "\t\t  " + WHITE_BRIGHT + "Created by Martin Buzogan" + ANSI_RESET);
        System.out.println(WHITE_bold_BRIGHT +"\t\t\t" +
                "AverageRating : "+ ratingService.getAverageRating(gameName)+"/5" + ANSI_RESET);
        System.out.println(WHITE_bold_BRIGHT + underline + ANSI_RESET);
        System.out.println("\t\t\t\t" + "  " + WHITE_bold_BRIGHT + "-1- Play -1-");
        System.out.println("\t\t\t\t" + "-2- FieldSize -2-");
        System.out.println("\t\t\t\t" + "  " + "-3- Quit -3-" + ANSI_RESET);
    }
    private void saveScore(String UserName){
        scoreService.addScore(new Score(gameName,UserName,gameField.getScore(),new Date()));
    }
    private void saveRating(int rating,String UserName){
        ratingService.setRating(new Rating(gameName,UserName,rating,new Date()));
    }
    private void saveComment(String comment,String UserName){
        commentService.addComment(new Comment(gameName,UserName,comment,new Date()));
    }

    public void play() throws IOException, URISyntaxException {
        int playAgain = 0;
        do{
            System.out.println("\n\n\n\n\n");
            int playerChoice = gameMenuInput();
            System.out.println();
            if (playerChoice == 3)
                return;
            if (playerChoice == 2) {
                changeFieldSize();
            }

            do {
                displayField();
                int firstTileIndex = getFieldInputFromUser();
                if (firstTileIndex == -1)
                    break;
                if(gameField.RevealTile(firstTileIndex) == -1)
                    System.out.println(ANSI_RED+"Tile already revealed"+ANSI_RESET);
            } while (gameField.getFieldState() == FieldState.PLAYING);

            if (gameField.getFieldState() == FieldState.SOLVED) {
                System.out.println("\t\t\033[1;37;44m" + WHITE_bold_BRIGHT + "--- Congratulations! You found all the cards! ---\033[0m");
            }
            addScore();
            addRating();
            addComment();
            System.out.println();
            playAgain = getPlayAgain();
        }while(playAgain==1);
    }

    private int getPlayAgain() throws IOException, URISyntaxException {
        gameField = new Field(6, 6);
        int playAgain;
        System.out.println("Do you want to play again ? \n  " +
                ANSI_GREEN+"Yes(1)"+WHITE_BRIGHT+"/"+ANSI_RED+"No(0)"+ANSI_RESET);
        playAgain = numberInput(Pattern.compile("[0-1]"),"choice");
        return playAgain;
    }

    private void addComment() {
        System.out.println("Do you want to leave a comment ? \n  " +
                ANSI_GREEN+"Yes(1)"+WHITE_BRIGHT+"/"+ANSI_RED+"No(0)"+ANSI_RESET);
        int userChoice = numberInput(Pattern.compile("[0-1]"),"choice");
        if(userChoice == 0)
            return;

        System.out.print("Enter comment : ");
        saveComment(scanner.nextLine().trim(),UserName);
        printComments();
    }

    private void addRating() {
        System.out.println("Do you want to leave a rating ? \n  " +
                ANSI_GREEN+"Yes(1)"+WHITE_BRIGHT+"/"+ANSI_RED+"No(0)"+ANSI_RESET);
        int userChoice = numberInput(Pattern.compile("[0-1]"),"choice");
        if(userChoice == 0)
            return ;
        saveRating(numberInput(Pattern.compile("[0-5]"),"rating"),UserName);
    }

    private void addScore() {
        System.out.println("Do you want to change your UserName ? (Score will be saved with default system name '"+System.getProperty("user.name")+"')\n  " +
                ANSI_GREEN+"Yes(1)"+WHITE_BRIGHT+"/"+ANSI_RED+"No(0)"+ANSI_RESET);
        int userChoice = numberInput(Pattern.compile("[0-1]"),"choice");
        UserName = System.getProperty("user.name");
        if(userChoice == 1) {
            System.out.print("Enter UserName : ");
            UserName = scanner.nextLine().trim();
        }
        saveScore(UserName);
        printScores();
    }

    private void changeFieldSize() {
        boolean fieldChanged = false;
        do {
            int rows = numberInput(FieldSizePattern,"number of rows");
            int columns = numberInput(FieldSizePattern,"number of columns");
            try {
                gameField = new Field(rows, columns);
                fieldChanged = true;
                System.out.println(ANSI_GREEN+"\t\t\tField size successfully updated\n"+ANSI_RESET);
            } catch (IllegalArgumentException error) {
                System.out.println(ANSI_RED+"\t\t\t"+error.getMessage()+ANSI_RESET);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }while(!fieldChanged);
        calculateFiedlInputRegex();
    }

    private int getFieldInputFromUser() {
        boolean correctInput = false;
        String xPosition = null, yPosition = null;
        do {
            System.out.print("Enter position: ");
            String input = scanner.nextLine().toUpperCase();
            var matcher = PositionPattern.matcher(input);
            if (matcher.matches()) {
                correctInput = true;
                xPosition = matcher.group(1);
                yPosition = matcher.group(2);
            } else {
                System.out.println(ANSI_RED+"Wrong input."+ANSI_RESET);
            }
        } while (!correctInput);
        return getIndexFromInput(xPosition, yPosition);
    }

    private int getIndexFromInput(String row, String column) {
        if (row == null || column == null) return -1;
        return ((((int) row.charAt(0) - 65) * gameField.getColumnCount()) + Integer.parseInt(column) - 1);
    }

    private void displayField() {
        for (int row = 0, arrayindex = 0; row < gameField.getRowCount(); row++) {
            System.out.print("\t\t\t" + (char) (65 + row) + " | ");
            for (int column = 0; column < gameField.getColumnCount(); column++) {
                arrayindex = displayTiles(arrayindex);
            }
            System.out.println();
        }
        displayColumnNumbers();
        System.out.println();
    }

    private void displayColumnNumbers() {
        System.out.print("\t\t\t" + "   ");
        for (int column = 0; column < gameField.getColumnCount(); column++)
            System.out.print(" -  ");
        System.out.println();
        System.out.print("\t\t\t" + "   ");
        for (int column = 1; column < gameField.getColumnCount() + 1; column++)
            System.out.print(" " + column + "  ");
    }

    private int displayTiles(int arrayindex) {
        if (gameField.getTile(arrayindex).getState() == TileState.FOUNDED) {
            System.out.print(ANSI_GREEN+" " + gameField.getTile(arrayindex).getValue() + "  "+ANSI_RESET);
        } else if(gameField.getTile(arrayindex).getState() == TileState.REVEALED)
            System.out.print(WHITE_BRIGHT+" " + gameField.getTile(arrayindex).getValue() + "  "+ANSI_RESET);
        else System.out.print((arrayindex % 2 == 0 ? Hidden_Tiles : Hidden_Tiles1) + " ");
        arrayindex++;
        return arrayindex;
    }
    private void printScores() {
        var scores = scoreService.getTopScores(gameName);
        System.out.println(WHITE_BRIGHT +"---------------------------------------------------------------");
        System.out.println("\t\t\t --- LeaderBoard ---"+ANSI_RESET);
        for (int i = 0; i < scores.size(); i++) {
            var score = scores.get(i);
            System.out.print(i % 2 == 0 ? ANSI_RESET : WHITE_BRIGHT);
            System.out.printf("\t%2d.| %s %d\n", i + 1, score.getPlayer(), score.getPoints());
        }
        System.out.println("---------------------------------------------------------------"+ANSI_RESET);
    }
    private void printComments() {
        var comments = commentService.getComments(gameName);
        System.out.println(WHITE_BRIGHT +"---------------------------------------------------------------");
        System.out.println("\t\t\t --- Comments ---"+ANSI_RESET);
        for (int i = 0; i < comments.size(); i++) {
            var comment = comments.get(i);
            System.out.print(i % 2 == 0 ? ANSI_RESET : WHITE_BRIGHT);
            System.out.printf("\t%2d.| %s : %s\n", i + 1, comment.getPlayer(), comment.getComment());
        }
        System.out.println("---------------------------------------------------------------"+ANSI_RESET);
    }

}
