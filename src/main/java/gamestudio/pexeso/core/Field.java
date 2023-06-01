package gamestudio.pexeso.core;

import gamestudio.pexeso.Main;

import javax.sound.midi.Soundbank;
import javax.xml.bind.SchemaOutputResolver;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Field {
    private final int rowCount;
    private final int columnCount;
    private final List<Tile> tiles;
    private List<Tile> revealedTiles;
    private FieldState fieldState = FieldState.PLAYING;
    private int foundedTiles;
    private long startMillis;
    private final String pathImages = "/static/images/Tiles" ;
    private List<String> imagePaths;
    private int imgError = 0;

    public Field(int rowCount, int columnCount) throws IllegalArgumentException, IOException, URISyntaxException {
        if ((rowCount * columnCount) % 2 != 0)
            throw new IllegalArgumentException("Size of gameField must be even");
        if ((rowCount * columnCount) > 60)
            throw new IllegalArgumentException("Size of gameField too big");

        this.rowCount = rowCount;
        this.columnCount = columnCount;
        try {
            imagePaths = getImagePathsFromResources(pathImages);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Error while loading images");
            imgError = 1;
        }
        this.tiles = new ArrayList<>();
        this.revealedTiles = new ArrayList<>();
        generateGameField();
    }
    private static List<String> getImagePathsFromResources(String folderPath) throws IOException, URISyntaxException {
        URL resourceUrl = Field.class.getResource(folderPath);
        Path resourcePath = Paths.get(resourceUrl.toURI());

        return Files.list(resourcePath)
                .filter(path -> path.toString().toLowerCase().endsWith(".png") ||
                        path.toString().toLowerCase().endsWith(".jpg") ||
                        path.toString().toLowerCase().endsWith(".jpeg") ||
                        path.toString().toLowerCase().endsWith(".gif"))
                .map(path -> {
                    String imagePath = path.toString();
                    int index = imagePath.indexOf("\\images");
                    if (index >= 0) {
                        imagePath = imagePath.substring(index);
                    }
                    return imagePath;
                })
                .collect(Collectors.toList());
    }

    public FieldState getFieldState() {
        return fieldState;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public int getRowCount() {
        return rowCount;
    }

    public int RevealTile(int index) {
        Tile tileToReveal = getTile(index);
        if(tileToReveal.getState() == TileState.FOUNDED || tileToReveal.getState() == TileState.REVEALED)
            return -1;

        tiles.get(index).setState(TileState.REVEALED);
        if((revealedTiles.size()+1)==2) {
            revealedTiles.add(getTile(index));
            tilesMatches();
            return 1;
        }
        else if((revealedTiles.size()+1)>2){
            revealedTiles.forEach(tile -> tile.setState(TileState.HIDDEN));
            revealedTiles.clear();
        }
        revealedTiles.add(getTile(index));
        return 1;
    }

    private void tilesMatches() {
        Tile firstTile = revealedTiles.get(0);
        Tile secondTile = revealedTiles.get(1);
        if (firstTile.getValue() == secondTile.getValue()) {
            firstTile.setState(TileState.FOUNDED);
            secondTile.setState((TileState.FOUNDED));
            revealedTiles.remove(firstTile);
            revealedTiles.remove(secondTile);
            foundedTiles++;
            ChangeFieldState();
        }
    }

    public int getFoundedTiles() {
        return foundedTiles;
    }


    private void ChangeFieldState() {
        if (FieldIsFounded())
            fieldState = FieldState.SOLVED;
    }

    private boolean FieldIsFounded() {
        for (Tile tile : tiles) {
            if (tile.getState() != TileState.FOUNDED)
                return false;
        }
        return true;
    }

    public Tile getTile(int index) {
        return tiles.get(index);
    }

    private void generateGameField() {
        generateTypesOfTiles();
        Collections.shuffle(tiles);
        startMillis = System.currentTimeMillis();
    }

    private void generateTiles(Character typeOfTile,int index) {
        this.tiles.add(new Tile(typeOfTile,imagePaths.get(index)));
        this.tiles.add(new Tile(typeOfTile,imagePaths.get(index)));
    }
    private void generateTiles(Character typeOfTile) {
        this.tiles.add(new Tile(typeOfTile));
        this.tiles.add(new Tile(typeOfTile));
    }

    private void generateTypesOfTiles() {
        List<Character> tileList = new ArrayList<>();
        Random random = new Random();
        for (int numberOfTiles = 0; numberOfTiles < ((rowCount * columnCount) / 2); ) {
            char randomAscii = (char) (random.nextInt(30) + 60);
            if (!tileList.contains(randomAscii)) {
                tileList.add(randomAscii);
                numberOfTiles++;
            }
        }
        if(imgError == 1){
            tileList.forEach(this::generateTiles);
        }else {
            AtomicInteger index = new AtomicInteger();
            tileList.forEach((n) -> {
                generateTiles(n, index.get());
                index.getAndIncrement();
            });
        }
    }

    public int getScore() {
        int minusPlayTime;
        if(((System.currentTimeMillis() - startMillis) / 10000) > (rowCount * columnCount))
            minusPlayTime = rowCount*columnCount;
        else
            minusPlayTime = (int) ((System.currentTimeMillis() - startMillis) / 10000);
        return (fieldState == FieldState.SOLVED ?
                (rowCount * columnCount - minusPlayTime) : 0)+getFoundedTiles();
    }
}
