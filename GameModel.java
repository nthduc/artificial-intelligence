package colat2;
import java.awt.Color;
import java.awt.Point;
import java.util.List;

public class GameModel {
	
	public GameModel() {
		
	}
	
	public enum Type {
		NONE,
		PLAYER,
		BOT
	}
	
	public enum GameState {
		NONE,
		DRAW,
		WIN,
		LOSE
	}
	
	
	
    public interface GameListener {

        void onUpdateUI(Color color, List<Point> points);

        void onGameOver(GameState state);
    }
}
