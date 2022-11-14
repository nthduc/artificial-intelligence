package colat;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import colat.GameModel.GameState;

public class GameFrameView extends JFrame implements GameModel.GameListener{
	
	private List<JButton> buttons;
	
	private GameController gameController;
	
	// Constructor
	public GameFrameView(String title) {
		super(title);
		initialize();
		display();
		
	}
	// Hàm khởi tạo
	public void initialize() {
		gameController = new GameController(this);
		buttons = new ArrayList<>();
		
		JPanel panel = new JPanel();
		//Grid theo hình chữ nhật kích thước 8 x 8 
		panel.setLayout(new GridLayout(GameController.SIZE, GameController.SIZE));
		
		for (int i = 0; i < GameController.SIZE; i++) {
			for (int j = 0; j < GameController.SIZE; j++) {
				JButton button = new JButton();
				button.setBackground(GameController.COLORS[gameController.getType(i, j)]);
				button.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						
						
					}
				});
				
				buttons.add(button);
				panel.add(button);
			}
		}
		
		getContentPane().add(panel);
	}
	
	public void display() {
		setSize(GameController.SIZE * 60 + 6, GameController.SIZE * 60 + 29);
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	private JButton getButton(int row, int col) {
        if (row >= 0 && row < GameController.SIZE && col >= 0 && col < GameController.SIZE) {
            return buttons.get(row * GameController.SIZE + col);
        }

        return null;
    }
	
	
	@Override
	public void onUpdateUI(Color color, List<Point> points) {
		if(points == null || color == null) return;
		
		for (Point point: points) {
			JButton button = getButton(point.x, point.y);
			
			if(button != null) {
				button.setBackground(color);
			}
		}
		
	}
	@Override
	public void onGameOver(GameState state) {
		JOptionPane.showMessageDialog(this, state.name(), "Trò chơi kết thúc !", JOptionPane.INFORMATION_MESSAGE);
		System.exit(0);
	}
}
