package colat2;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;



public class GameFrameView extends JFrame implements GameModel.GameListener{
	   private List<JButton> buttons;

	    private GameController controller;

	    public GameFrameView(String title) {
	        super(title);

	        initialize();
	    }

	    private void initialize() {
	        controller = new GameController(this);
	        buttons = new ArrayList<>();

	        JPanel panel = new JPanel();
	        panel.setLayout(new GridLayout(GameController.SIZE, GameController.SIZE));

	        for (int i = 0; i < GameController.SIZE; i++) {
	            for (int j = 0; j < GameController.SIZE; j++) {
	                JButton button = createButton(i, j);
	                buttons.add(button);
	                panel.add(button);
	            }
	        }

	        getContentPane().add(panel);

	        setSize(GameController.SIZE * 60 + 6, GameController.SIZE * 60 + 29);
	        setResizable(false);
	        setLocationRelativeTo(null);
	        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	    }

	    private JButton createButton(int row, int col) {
	        JButton button = new JButton();
	        button.setBackground(GameController.COLORS[controller.getType(row, col)]);
	        button.addActionListener(e -> {
	            controller.play(row, col);
	        });
	        return button;
	    }

	    private JButton getButton(int row, int col) {
	        if (row >= 0 && row < GameController.SIZE && col >= 0 && col < GameController.SIZE) {
	            return buttons.get(row * GameController.SIZE + col);
	        }

	        return null;
	    }

	    @Override
	    public void onUpdateUI(Color color, List<Point> points) {
	        if (points == null || color == null) {
	            return;
	        }

	        for (Point point : points) {
	            JButton button = getButton(point.x, point.y);
	            if (button != null) {
	                button.setBackground(color);
	            }
	        }
	    }

	    @Override
	    public void onGameOver(GameController.GameState state) {
	        JOptionPane.showMessageDialog(this, state.name(), "Game Over", JOptionPane.INFORMATION_MESSAGE);
	        System.exit(0);
	    }
}
