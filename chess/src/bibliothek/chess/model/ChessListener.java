package bibliothek.chess.model;

public interface ChessListener {
    public void killed( int r, int c, Figure figure );
    public void moved( int sr, int sc, int dr, int dc, Figure figure );
    public void playerSwitched( Player player );
}
