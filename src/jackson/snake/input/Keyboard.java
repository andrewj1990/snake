package jackson.snake.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Keyboard implements KeyListener {

	public boolean[] keys = new boolean[120];
	public boolean up, down, left, right, space;
	
	public void update() {
		up = keys[KeyEvent.VK_UP];
		down = keys[KeyEvent.VK_DOWN];
		left = keys[KeyEvent.VK_LEFT];
		right = keys[KeyEvent.VK_RIGHT];
		space = keys[KeyEvent.VK_SPACE];
		
//		for (int i = 0; i < keys.length; i++) {
//			if (keys[i]) {
//				System.out.println(i);
//			}
//		}
		
	}
	
	public void keyPressed(KeyEvent e) {
		if (up && e.getKeyCode() == KeyEvent.VK_DOWN) return;
		if (down && e.getKeyCode() == KeyEvent.VK_UP) return;
		if (right && e.getKeyCode() == KeyEvent.VK_LEFT) return;
		if (left && e.getKeyCode() == KeyEvent.VK_RIGHT) return;
		// set everything to false except the key pressed
		for (int i = 0; i < keys.length; i++) {
			keys[i] = false;
		}
		// leave the key pressed as true even if released
		keys[e.getKeyCode()] = true;
		
	}
	
	public void keyReleased(KeyEvent e) {
//		keys[e.getKeyCode()] = false;
	}

	public void keyTyped(KeyEvent e) {
		
	}

}
