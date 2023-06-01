package gamestudio.pexeso;

import gamestudio.pexeso.ConsoleUI.ConsoleUI;
import gamestudio.pexeso.core.Field;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IllegalAccessException, IOException, URISyntaxException {
        Field field = new Field(8, 7);
        ConsoleUI ui = new ConsoleUI(field);
        ui.play();
    }
}