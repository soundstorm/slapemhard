package de.hshannover.pp.slapemhard;

import javax.swing.*;

import de.hshannover.pp.slapemhard.images.BufferedImageLoader;
import de.hshannover.pp.slapemhard.listener.*;
import de.hshannover.pp.slapemhard.objects.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.logging.Logger;

public class Menu implements Runnable {
	private static final Logger log = Logger.getLogger(Person.class.getName());
	
	private JFrame frame;
	private double scale;
	private Dimension gameSize;
	private int credits;
	private Thread thread;
	private Game game;
	private Font font;
	private int positionFrame;
	private ArrayList<Integer> activeSelection = new ArrayList<Integer>();
	private final BufferedImageLoader bL = new BufferedImageLoader();
	private final BufferedImage howtoplay = bL.getImage("/controls.png");
	private final BufferedImage background = bL.getImage("/startscreen.png");

	public Menu(JFrame frame, Dimension gameSize, double scale) {
		this.frame = frame;
		this.gameSize = gameSize;
		this.scale = scale;
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/res/fonts/8-BIT-WONDER.TTF"));
		} catch (IOException|FontFormatException e) {
			System.out.println("Failed to load font");
		}
		activeSelection.add(0);
	}

	public synchronized void start(){
		if(game != null){
			System.out.println("Game alread running");
			return;
		}
		thread = new Thread(this, "Game Thread");
		thread.start();
	}
	
	@Override
	public void run() {
		game = new Game(this);
		//game.start();
		synchronized (this) {
			try {
				this.wait();
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
		game = null;
	}
	
	public int getActiveMenu() {
		return 0;
	}
	public int getActiveOption() {
		return activeSelection.get(activeSelection.size()-1);
	}
	
	public void render(Graphics g) {
		//Main Menu
		//Background
		g.drawImage(background, 0, 0, null);
		if (activeSelection.size()==1) {
			//Header
			g.setFont(font.deriveFont(Font.PLAIN,18));
			
			g.setColor(getActiveOption()==0?Color.YELLOW:Color.WHITE);
				g.fillRect((gameSize.width-200)/2, 80, 200, 20);
				g.setColor(Color.BLACK);
				drawStringCentered(g,"New Game",98);
				//g.drawString("New Game",(gameSize.width-160)/2+2,118);
			
			g.setColor(getActiveOption()==1?Color.YELLOW:Color.WHITE);
				g.fillRect((gameSize.width-200)/2, 105, 200, 20);
				g.setColor(Color.BLACK);
				drawStringCentered(g,"How to play",123);
				//g.drawString("How to play",(gameSize.width-190)/2+2,158);
				
			g.setColor(getActiveOption()==2?Color.YELLOW:Color.WHITE);
				g.fillRect((gameSize.width-200)/2, 130, 200, 20);
				g.setColor(Color.BLACK);
				drawStringCentered(g,"Credits",148);
				//g.drawString("Credits",(gameSize.width-120)/2+2,198);
				
			g.setColor(getActiveOption()==3?Color.YELLOW:Color.WHITE);
				g.fillRect((gameSize.width-200)/2, 155, 200, 20);
				g.setColor(Color.BLACK);
				drawStringCentered(g,"Run away",173);
				//g.drawString("Credits",(gameSize.width-120)/2+2,198);
		} else
			//SUBMENUS
			if (activeSelection.get(0) == 1) {
				g.drawImage(howtoplay, 0-positionFrame, 40, null);
				if (positionFrame != activeSelection.get(1)*320) {
					positionFrame += Math.signum(activeSelection.get(1)*320-positionFrame)*20;
				}
			} else {
				g.setColor(Color.BLACK);
				g.setFont(font.deriveFont(Font.PLAIN,18));
				drawStringCentered(g,"* * Credits * *",90);
				drawStringCentered(g,"Patrick Defayay",120);
				drawStringCentered(g,"Andre Schmidt",150);
				drawStringCentered(g,"Steffen Schulz",180);
				drawStringCentered(g,"Luca Zimmermann",210);
			}
	}
	
	public BufferedImage getBackground() {
		return background;
	}
	
	private void drawStringCentered(Graphics g, String string, int y) {
		g.drawString(string, (gameSize.width-g.getFontMetrics().stringWidth(string))/2, y);
	}

	public JFrame getFrame() {
		return frame;
	}
	public Dimension getGameSize() {
		return gameSize;
	}
	public double getScale() {
		return scale;
	}
	public int getCredits() {
		return credits;
	}
	public void addCredits() {
		log.info("Inserted coin");
		credits++;
	}
	public boolean useCredit() {
		if (credits == 0) {
			return false;
		}
		credits--;
		return true;
	}
	
	public Game getGame() {
		return game;
	}
	public Level getLevel() {
		if (game != null) {
			return game.getLevel();
		}
		return null;
	}
	/**
	 * Handles Keyboard Events fired by {@link KeyboardListener#keyPressed(KeyEvent)}
	 * @param keyCode corresponding code of the key.
	 */
	public void keyEvent(int keyCode) {
		switch (keyCode) {
			case KeyEvent.VK_UP:
				if (activeSelection.size() == 1) {
					activeSelection.set(0,(activeSelection.get(0)+3)%4);
				}
				break;
			case KeyEvent.VK_DOWN:
				if (activeSelection.size() == 1) {
					activeSelection.set(0,(activeSelection.get(0)+1)%4);
				}
				break;
			case KeyEvent.VK_LEFT:
				if (activeSelection.get(0) == 1 && activeSelection.size()!=1) {
					activeSelection.set(1,(activeSelection.get(1)+1)%2);
				}
				break;
			case KeyEvent.VK_RIGHT:
				if (activeSelection.get(0) == 1 && activeSelection.size()!=1) {
					activeSelection.set(1,(activeSelection.get(1)+1)%2);
				}
				break;
			case KeyEvent.VK_ENTER: case KeyEvent.VK_CONTROL: case KeyEvent.VK_SHIFT:
				if (activeSelection.get(0) == 0) {
					start();
					break;
				} else if (activeSelection.get(0) == 3) {
					close();
					break;
				}
				if (activeSelection.size() == 1) {
					activeSelection.add(0);
					positionFrame = 0;
				}
				break;
			case KeyEvent.VK_ESCAPE: case KeyEvent.VK_ALT: case KeyEvent.VK_Y:
				if (activeSelection.size() == 1) {
					if (keyCode == 27) {
						close();
					}
					break;
				}
				activeSelection.remove(activeSelection.size()-1);
				break;
		}
	}
	/**
	 * Tries to close the Window, which will be caught by {@link SlapEmHard} and will open a confirm dialog.
	 */
	private void close() {
		((Window)frame).getToolkit().getSystemEventQueue().postEvent(new WindowEvent((Window)frame, WindowEvent.WINDOW_CLOSING));
	}
}
