package colat;

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
		// Hàm xử lí cập nhật
		void onUpdateUI(Color color, List<Point> points);
		
		// Hàm xử lí check Game kết thúc
		void onGameOver(GameState state);
		
	}
}
