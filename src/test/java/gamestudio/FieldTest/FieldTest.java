package gamestudio.FieldTest;

import gamestudio.pexeso.core.Field;
import gamestudio.pexeso.core.FieldState;
import gamestudio.pexeso.core.TileState;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FieldTest {
    @Test
    /**
     * Field Too big
     */
    void generateFieldBadParamsTooBig() {
        assertThrows(IllegalArgumentException.class,
                () -> new Field(10, 9));
    }

    /**
     * Field must be even
     */
    @Test
    void generateFieldBadParamsOddNumberOfTiles() {
        assertThrows(IllegalArgumentException.class,
                () -> new Field(3, 3));
    }
    @Test
    void revealTile() throws IOException, URISyntaxException {
        int rows = 4;
        int columns = 4;
        var field = new Field(rows,columns);
        Random random = new Random();
        int randomTile = random.nextInt(rows*columns);
        assertSame(field.getTile(randomTile).getState(), TileState.HIDDEN);
        field.RevealTile(randomTile);
        assertSame(field.getTile(randomTile).getState(), TileState.REVEALED);
    }
    @Test
    void fieldSolved() throws IOException, URISyntaxException {
        int rows = 2;
        int columns = 1;
        var field = new Field(rows,columns);
        assertSame(field.getFieldState(), FieldState.PLAYING);
        field.RevealTile(0);
        field.RevealTile(1);
        assertSame(field.getFieldState(), FieldState.SOLVED);
    }
    @Test
    void revealRevealedTiles() throws IOException, URISyntaxException {
        int rows = 2;
        int columns = 1;
        var field = new Field(rows,columns);
        field.RevealTile(0);
        assertSame(field.RevealTile(0), -1);
    }
    @Test
    void revealFoundedTiles() throws IOException, URISyntaxException {
        int rows = 2;
        int columns = 2;
        var field = new Field(rows,columns);
        Character firstTileValue = field.getTile(0).getValue();
        field.RevealTile(0);
        int tileIndex = 1;
        for(; tileIndex <rows*columns;tileIndex++) {
            if(field.getTile(tileIndex).getValue() == firstTileValue){
                field.RevealTile(tileIndex);
                break;
            }
        }
        assertSame(field.getTile(0).getState(), TileState.FOUNDED);
        assertSame(field.getTile(tileIndex).getState(), TileState.FOUNDED);
        assertSame(field.RevealTile(0), -1);
    }
    @Test
    void uniqeTiles() throws IOException, URISyntaxException {
        int rows = 4;
        int columns = 4;
        var field = new Field(rows,columns);
        List<Character> tileValues = new ArrayList<>();
        for(int tileIndex = 0; tileIndex <rows*columns;tileIndex++) {
            if(!tileValues.contains(field.getTile(tileIndex).getValue())){
                tileValues.add(field.getTile(tileIndex).getValue());
            }
        }
        assertSame(tileValues.size(), (rows*columns)/2);
    }
}
