package colat2;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class GameController extends GameModel{

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
                playBot();
            }
        }, 300);
    }

    // Thực hiện việc chọn ô của BOT
    private void playBot() {
        Map.Entry<Point, List<Point>> action = getBestAction(board);
        if (action != null) {
            List<Point> points = action.getValue();
            points.add(action.getKey());

            updateBoard(board, points, Type.BOT);

            GameState state = checkEnd(board);
            if (state != GameState.NONE && listener != null) {
                listener.onGameOver(state);
                return;
            }

            Map<Point, List<Point>> moves = getPossibleActions(board, Type.PLAYER);
            if (moves.isEmpty()) {
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        playBot();
                    }
                }, 300);
            }
        } else {
            GameState state = checkEnd(board);
            if (state != GameState.NONE && listener != null) {
                listener.onGameOver(state);
            }
        }
    }

    // Lấy vị trí đánh tốt nhất của BOT
    private Map.Entry<Point, List<Point>> getBestAction(Type[][] board) {
        Map<Point, List<Point>> actions = getPossibleActions(board, Type.BOT);
        if (actions.isEmpty()) {
            return null;
        }

        double bestScore = Double.MIN_VALUE;
        Map.Entry<Point, List<Point>> bestAction = null;
        for (Map.Entry<Point, List<Point>> action : actions.entrySet()) {
            Type[][] tempBoard = simulateAction(board, action, Type.BOT);
            double score = minimax(tempBoard, 3, false);
            if (score > bestScore) {
                bestScore = score;
                bestAction = action;
            }
        }

        return bestAction;
    }

    // Giải thuật minimax cho BOT
    private double minimax(Type[][] board, int depth, boolean isMax) {
        GameState state = checkEnd(board);
        if (state == GameState.WIN) {
            return 0;
        } else if (state == GameState.LOSE) {
            return Double.MAX_VALUE;
        } else if (state == GameState.DRAW) {
            return 1;
        }

        if (depth == 0) {
            return evaluateScore(board);
        }

        Map<Point, List<Point>> actions;
        double bestScore;

        if (isMax) {
            bestScore = Double.MIN_VALUE;
            actions = getPossibleActions(board, Type.BOT);
            for (Map.Entry<Point, List<Point>> action : actions.entrySet()) {
                Type[][] tempBoard = simulateAction(board, action, Type.BOT);
                double score = minimax(tempBoard, depth - 1, false);
                bestScore = Math.max(bestScore, score);
            }
        } else {
            bestScore = Double.MAX_VALUE;
            actions = getPossibleActions(board, Type.PLAYER);
            for (Map.Entry<Point, List<Point>> action : actions.entrySet()) {
                Type[][] tempBoard = simulateAction(board, action, Type.PLAYER);
                double score = minimax(tempBoard, depth - 1, true);
                bestScore = Math.min(bestScore, score);
            }
        }

        return bestScore;
    }

    // Tính điểm hiện tại của trò chơi phục vụ cho minimax với điểm là tỉ lệ chiếm đóng của BOT với người chơi
    private double evaluateScore(Type[][] board) {
        double countB = 0, countW = 0;

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == Type.PLAYER) {
                    countB++;
                } else if (board[i][j] == Type.BOT) {
                    countW++;
                }
            }
        }

        return countW / countB;
    }

    // Kiểm tra việc trò chơi kết thúc
    private GameState checkEnd(Type[][] board) {
        int countB = 0, countW = 0, countN = 0;

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == Type.PLAYER) {
                    countB++;
                } else if (board[i][j] == Type.BOT) {
                    countW++;
                } else {
                    countN++;
                }
            }
        }

        if (countB != 0 && countW != 0 && countN != 0 &&
                !getPossibleActions(board, Type.PLAYER).isEmpty() &&
                !getPossibleActions(board, Type.PLAYER).isEmpty()) {
            return GameState.NONE;
        }

        if (countB > countW) {
            return GameState.WIN;
        } else if (countB < countW) {
            return GameState.LOSE;
        } else {
            return GameState.DRAW;
        }
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

    // Mô phỏng việc chọn một ô của người chơi hoặc BOT
    private Type[][] simulateAction(Type[][] board, Map.Entry<Point, List<Point>> actions, Type type) {
        Type[][] newBoard = new Type[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            System.arraycopy(board[i], 0, newBoard[i], 0, SIZE);
        }

        if (actions == null) {
            return newBoard;
        }

        newBoard[actions.getKey().x][actions.getKey().y] = type;
        for (Point point : actions.getValue()) {
            newBoard[point.x][point.y] = type;
        }

        return newBoard;
    }

    // Lấy tất cả các ô có thể chọn của người chơi hoặc BOT
    private Map<Point, List<Point>> getPossibleActions(Type[][] board, Type type) {
        Map<Point, List<Point>> moves = new HashMap<>();

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == Type.NONE) {
                    List<Point> points = count(board, i, j, type);
                    if (!points.isEmpty()) {
                        moves.put(new Point(i, j), points);
                    }
                }
            }
        }

        return moves;
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


