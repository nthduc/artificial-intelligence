package colat;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


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
	 // Thực hiện việc chọn ô của người chơi
	    public void play(int row, int col) {
	    	if (row < 0 || row >= SIZE || col < 0 || col >= SIZE || board[row][col] != Type.NONE) {
	            return;
	    	}
	    	List<Point> points = count(board, row, col, Type.PLAYER);
	        if (points.isEmpty()) {
	            return;
	    }
	        
	        points.add(new Point(row, col));
	        
	        updateBoard(board, points, Type.PLAYER);
	       
	        timer.schedule(new TimerTask() {
	            @Override
	            public void run() {
//	                playBot();
	            }
	        }, 300);
}
	    // Cập nhật trạng thái của trò chơi
	    private void updateBoard(Type[][] board, List<Point> points, Type type) {
		  if (points == null || points.isEmpty()) {
	            return;
	        }

	        for (Point point : points) {
	            board[point.x][point.y] = type;
	        }

	        if (listener != null) {
	            listener.onUpdateUI(COLORS[type.ordinal()], points);
	        }
	}
	    // Đếm số ô có thể ăn được của người chơi hoặc BOT khi đánh vào ô(row, col)
	    private List<Point> count(Type[][] board, int row, int col, Type type) {
		
	    	 if (type == Type.NONE) {
	             return new ArrayList<>();
	         }

	         List<Point> points = new ArrayList<>();

	         for (int i = 0; i < ROW.length; i++) {
	             points.addAll(getPoints(board, row, col, i, type));
	         }

	         return points;
	}
	    
	    // Đếm số ô có thể ăn được của người chơi hoặc BOT khi đánh vào ô(row, col) theo hướng
	    private List<Point> getPoints(Type[][] board, int row, int col, int direction, Type type) {
	        List<Point> points = new ArrayList<>();

	        int newRow = row + ROW[direction];
	        int newCol = col + COL[direction];
	        while (newRow >= 0 && newRow < SIZE && newCol >= 0 && newCol < SIZE && board[newRow][newCol] != Type.NONE) {
	            if (board[newRow][newCol] == type) {
	                return points;
	            }

	            points.add(new Point(newRow, newCol));
	            newRow = newRow + ROW[direction];
	            newCol = newCol + COL[direction];
	        }

	        return new ArrayList<>();
	    }
}
