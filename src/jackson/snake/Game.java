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
	private int snake_location_tail;
	private int score = -1;
	private int x = width / 2;
	private int y = height / 2;
	private int difficulty_counter = 0;
	private int random_wall = 0;
	private boolean food_flag = true;
	private boolean running = false;
	private boolean gameOver = false;
//	private int position;
	private List<Integer> snake_coordinates = new ArrayList<Integer>();
	private List<Integer> wall_coordinates = new ArrayList<Integer>();
	
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
			delta += (now - lastTime) / ns;
			lastTime = now;
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
		if (food_location == snake_location) {
			food_flag = true;
//			score++;
		}
		
		if (x < 0) gameOver = true;
		if (x > width) gameOver = true;
		if (y < 0) {
			y = 0;
			gameOver = true;
		}
		if (y > height - 1) {
			y = height - 1;
			gameOver = true;
		}
		snake_location = x + y * width;		
		// create the food
		if (food_flag) {
			food = random.nextInt(width * height);
			food_flag = false;
//			snake_location_tail = (x - 1) + (y - 1) * width;
			snake_coordinates.add(snake_location);
			difficulty_counter = 0;
			score++;
		} else {
			snake_coordinates.add(snake_location);
			snake_coordinates.remove(0);
			difficulty_counter++;
//			System.out.println(difficulty_counter);
		}
		
		if (difficulty_counter > 10) {
			random_wall = random.nextInt(width * (height - 1));
			wall_coordinates.add(random_wall);
			difficulty_counter = 0;
		}
		
		
		for (int i = 0; i < snake_coordinates.size() - 1; i++) {
			if (snake_location == snake_coordinates.get(i) && snake_location != food_location) {
				gameOver = true;
			}
//			System.out.println(snake_coordinates.get(i));
		}
		
		for (int i = 0; i < wall_coordinates.size(); i++) {
			if (snake_location == wall_coordinates.get(i)) {
				gameOver = true;
			}
		}
		
//		System.out.println("food: " +  food_location + " snake: " + snake_location);
	
//		System.out.println(x);
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
//		snake_location = x + y * width;		
//		// create the food
//		if (food_flag) {
//			food = random.nextInt(width * height);
//			food_flag = false;
////			snake_location_tail = (x - 1) + (y - 1) * width;
//			snake_coordinates.add(snake_location);
//			difficulty_counter = 0;
//			score++;
//		} else {
//			snake_coordinates.add(snake_location);
//			snake_coordinates.remove(0);
//			difficulty_counter++;
////			System.out.println(difficulty_counter);
//		}
//		
//		if (difficulty_counter > 1000) {
//			random_wall = random.nextInt(width * (height - 1));
//			wall_coordinates.add(random_wall);
//			difficulty_counter = 0;
//		}
//		snake_coordinates.add(10);
//		System.out.println(snake_coordinates.size());
//		snake_coordinates.remove(0);
		
		pixels[food] = 0xf6ff00;
		food_location = food;
//		snake_location = x + y * width;
//		snake_coordinates.add(snake_location);
//		System.out.println(snake_coordinates.size());
//		System.out.println("food: " + food2 + " snake location: " + loc);
		// create an array and for all the x and y coordinates add a white spot;
		for (int i = 0; i < snake_coordinates.size(); i++) {
			pixels[snake_coordinates.get(i)] = 0xffffff;
		}
		
		for (int i = 0; i < wall_coordinates.size(); i++) {
			pixels[wall_coordinates.get(i)] = 0xC69C6B;
		}
//		snake_coordinates.remove(0);
		
//		System.out.println(x);
		
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
			g.drawString("GAME OVER", getWidth()/4, getHeight()/2);
			g.drawString("Score: " + score, getWidth()/4 + 50, getHeight()/2 + 80);
			g.drawString("Press Spacebar to Restart", getWidth() / 4, getHeight() / 2 + 150);
			g.dispose();
			bs.show();
			key.update();
			if (key.space) {
				x = width / 2;
				y = height / 2;
				snake_coordinates.clear();
				wall_coordinates.clear();
				snake_coordinates.add(x * y * width);
				score = 0;
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
