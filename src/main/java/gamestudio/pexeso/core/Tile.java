package gamestudio.pexeso.core;

public class Tile {
    private TileState state = TileState.HIDDEN;
    private Character Type;
    private String imgPath;

    public Tile(Character type) {
        this.Type = type;
    }
    public Tile(Character type,String imgPath) {
        this.Type = type;
        this.imgPath = imgPath;
    }

    public String getImgPath() {
        return imgPath;
    }

    public Character getValue() {
        return Type;
    }

    protected void setState(TileState state) {
        this.state = state;
    }

    public TileState getState() {
        return state;
    }
}
