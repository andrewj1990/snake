package jackson.snake;

import jackson.snake.input.Keyboard;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;

public class Game extends Canvas implements Runnable {

	private static final long serialVersionUID = 1L;
	private int width = 100;
	private int height = width / 16 * 9;
	private int scale = 9;
	private int food;
	private int food_location;
	private int snake_location;
	private int score = -1;
	private int x = width / 2;
	private int y = height / 2;
	private int difficulty_counter = 0;
	private final int  initial_difficulty_meter = 100;
	private int difficulty_meter = initial_difficulty_meter;
	private int random_wall = 0;
	private int power_up;
	private int power_up_number = 0;
	private int clear_radius = 10;
	private boolean food_flag = true;
	private boolean running = false;
	private boolean gameOver = false;
//	private int position;
	private List<Integer> snake_coordinates = new ArrayList<Integer>();
	private List<Integer> wall_coordinates = new ArrayList<Integer>();
	private List<Integer> power_up_coordinates = new ArrayList<Integer>();
	
	private JFrame frame;
	private Thread thread;
	private Keyboard key;
	private Random random;
	
	private BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	private int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
	
	public Game() {
		Dimension size = new Dimension(width * scale, height * scale);
		setPreferredSize(size);
		
		frame = new JFrame();
		key = new Keyboard();
		addKeyListener(key);
		random = new Random();
	}
	
	public void start() {
		running = true;
		thread = new Thread(this, "Display");
		thread.start();
		
	}
	
	public void stop() {
		running = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		
		long lastTime = System.nanoTime();
		final double ns = 1000000000.0 / 25.0;
		double delta = 0;
		
		requestFocus();
		key.keys[KeyEvent.VK_UP] = true;
		while (running)	 {
//			System.out.println(key.up);
			long now = System.nanoTime();
//			System.out.println("now: " + now + " LastTime: " + lastTime);
			delta += (now - lastTime) / ns;
			
			lastTime = now;
//			System.out.println(delta);
			while (delta >= 1) {
				update();
				delta--;
			}
//			System.out.println(key.up);
			
//			update();
			
//			try {
//				Thread.sleep(50);
//			} catch (InterruptedException e) {
//				Thread.currentThread().interrupt();
//			}
			
			render();
			
//			try {
//				Thread.sleep(50);
//			} catch (InterruptedException e) {
//				Thread.currentThread().interrupt();
//			}
		}
	}
	
	public void update() {
		key.update();
		if (key.up) y--;
		if (key.down) y++;
		if (key.left) x--;
		if (key.right) x++; 
		if (food_location == snake_location) food_flag = true;
		
		// set the bounds so that you lose if you go across it
//		if (x < 0) gameOver = true;
//		if (x > width) gameOver = true;
//		if (y < 0) {
//			y = 0;
//			gameOver = true;
//		}
//		if (y > height - 1) {
//			y = height - 1;
//			gameOver = true;
//		}
		
		// allow snake to cross bounds
		if (x < 0) x = width - 1;
		if (x >= width) x = 0;
		if (y < 0) y = height - 1;
		if (y >= height) y = 0;
		
		snake_location = x + y * width;		
		// create the food
		if (food_flag) {
			food = random.nextInt(width * height);
			food_flag = false;
//			snake_location_tail = (x - 1) + (y - 1) * width;
			snake_coordinates.add(snake_location);
			difficulty_counter = 0;
			difficulty_meter = initial_difficulty_meter;
			score++;
		} else {
			snake_coordinates.add(snake_location);
			snake_coordinates.remove(0);
			difficulty_counter++;
//			System.out.println(difficulty_counter);
		}
		
		if (difficulty_counter > difficulty_meter) {
			random_wall = random.nextInt(width * (height - 1));
			wall_coordinates.add(random_wall);
			difficulty_counter = 0;
			if (difficulty_meter > 30) difficulty_meter = difficulty_meter * 3 / 4;
			power_up_number++;
		}
		
		if (power_up_number > 10) {
			power_up = random.nextInt(width * height);
			power_up_coordinates.add(power_up);
			power_up_number = 0;
		}
		
		for (int i = 0; i < snake_coordinates.size() - 1; i++) {
			if (snake_location == snake_coordinates.get(i) && snake_location != food_location) {
				gameOver = true;
			}

		}
		
		for (int i = 0; i < wall_coordinates.size(); i++) {
			if (snake_location == wall_coordinates.get(i)) {
				gameOver = true;
			}
		}
		
		for (int i = 0; i < power_up_coordinates.size(); i++) {
			if (snake_location == power_up_coordinates.get(i)) {
				// clear walls around power_up_coordinates.get(i)
				int q;
				System.out.println(power_up_coordinates.get(i));
				for (int j = 0; j < clear_radius; j++) {
					q = power_up_coordinates.get(i) + j;
					System.out.println(q);

				}
				power_up_coordinates.remove(i);
			}
		}

	}
	
	
	public void render() {
		BufferStrategy bs = getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(2);
			return;
		}
		
		// clear the screen
		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = 0;
		}

		pixels[food] = 0xf6ff00;
		food_location = food;

		// create an array and for all the x and y coordinates add a white spot;
		for (int i = 0; i < snake_coordinates.size(); i++) {
			pixels[snake_coordinates.get(i)] = 0xffffff;
		}
		
		for (int i = 0; i < wall_coordinates.size(); i++) {
			pixels[wall_coordinates.get(i)] = 0xC69C6B;
		}
		
		for (int i = 0; i < power_up_coordinates.size(); i++) {
			pixels[power_up_coordinates.get(i)] = 0xff0000;
		}

		Graphics g = bs.getDrawGraphics();
//		g.setColor(Color.BLACK);
//		g.fillRect(0, 0, getWidth(), getHeight());
		g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
		while (gameOver) {
			for (int i = 0; i < pixels.length; i++) {
				pixels[i] = 0;
			}
			
			for (int i = 0; i < key.keys.length; i++) {
				key.keys[i] = false;
			}
			
			g.setColor(Color.WHITE);
			g.setFont(new Font("TimesRoman", Font.PLAIN, 50));
			g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
			g.drawString("GAME OVER", getWidth() / 4, getHeight() / 2);
			g.drawString("Score: " + score, getWidth() / 4 + 50, getHeight() / 2 + 80);
			g.drawString("Press Spacebar to Restart", getWidth() / 4, getHeight() / 2 + 150);
			g.dispose();
			bs.show();
			key.update();
			if (key.space) {
				x = width / 2;
				y = height / 2;
				snake_coordinates.clear();
				wall_coordinates.clear();
				power_up_coordinates.clear();
				snake_coordinates.add(x * y * width);
				score = 0;
				difficulty_meter = initial_difficulty_meter;
				key.keys[KeyEvent.VK_UP] = true;
				gameOver = false;
			}
		}
		frame.setTitle("Score: " + score);
		g.dispose();
		bs.show();
		
		
	}
	
	public static void main(String[] args) {
		Game game = new Game();
		
		game.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		game.frame.setResizable(false);
		game.frame.add(game);
		game.frame.pack();
		game.frame.setVisible(true);
		game.frame.setLocationRelativeTo(null);
		
		game.start();
		
	}
	
}
