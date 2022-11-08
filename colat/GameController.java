package colat;

import java.awt.Color;
import java.util.Timer;

public class GameController extends GameModel {
	 public static final Color[] COLORS = new Color[]{Color.decode("#4caf50"), Color.BLACK, Color.WHITE};
	    public static final int SIZE = 8;

	    private static final int[] ROW = new int[]{-1, -1, 0, 1, 1, 1, 0, -1};
	    private static final int[] COL = new int[]{0, 1, 1, 1, 0, -1, -1, -1};

	    private final GameListener listener;

	    private final Type[][] board;

	    private Timer timer;
	    

	    // Khởi tạo các giá trị của trò chơi
	    public GameController(GameListener listener) {
	        this.listener = listener;

	        board = new Type[SIZE][SIZE];

	        for (int i = 0; i < SIZE; i++) {
	            for (int j = 0; j < SIZE; j++) {
	                board[i][j] = Type.NONE;
	            }
	        }

	        timer = new Timer("Game");

	        board[3][4] = board[4][3] = Type.PLAYER;
	        board[3][3] = board[4][4] = Type.BOT;
	    }
	    
	 // Lấy dữ liệu tại ô(row, col) - PLAYER: Người chơi, BOT: máy, NONE: Trống
	    public int getType(int row, int col) {
	        if (row >= 0 && row < SIZE && col >= 0 && col < SIZE) {
	            return board[row][col].ordinal();
	        } else {
	            return 0;
	        }
	    }
}
