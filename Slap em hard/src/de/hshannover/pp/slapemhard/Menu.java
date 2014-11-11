package de.hshannover.pp.slapemhard;

import javax.swing.*;

import de.hshannover.pp.slapemhard.listener.*;
import de.hshannover.pp.slapemhard.objects.*;

import java.awt.*;
import java.awt.event.*;
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
	private ArrayList<Integer> activeSelection = new ArrayList<Integer>();

	public Menu(JFrame frame, Dimension gameSize, double scale) {
		this.frame = frame;
		this.gameSize = gameSize;
		this.scale = scale;
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/res/fonts/VCR_OSD_MONO.ttf"));
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
		if (activeSelection.size()==1) {
			//Background
			g.setColor(Color.RED);
			g.fillRect(0,0,gameSize.width,gameSize.height);
			//Header
			g.setColor(Color.WHITE);
			g.setFont(font.deriveFont(Font.PLAIN,28));
			g.drawString("Slap Em Hard",2,50);
			
			g.setFont(font.deriveFont(Font.PLAIN,18));
			
			g.setColor(getActiveOption()==0?Color.YELLOW:Color.WHITE);
				g.fillRect((gameSize.width-200)/2, 100, 200, 20);
				g.setColor(Color.BLACK);
				drawStringCentered(g,"New Game",118);
				//g.drawString("New Game",(gameSize.width-160)/2+2,118);
			
			g.setColor(getActiveOption()==1?Color.YELLOW:Color.WHITE);
				g.fillRect((gameSize.width-200)/2, 140, 200, 20);
				g.setColor(Color.BLACK);
				drawStringCentered(g,"How to play",158);
				//g.drawString("How to play",(gameSize.width-190)/2+2,158);
				
			g.setColor(getActiveOption()==2?Color.YELLOW:Color.WHITE);
				g.fillRect((gameSize.width-200)/2, 180, 200, 20);
				g.setColor(Color.BLACK);
				drawStringCentered(g,"Credits",198);
				//g.drawString("Credits",(gameSize.width-120)/2+2,198);
				
			g.setColor(getActiveOption()==3?Color.YELLOW:Color.WHITE);
				g.fillRect((gameSize.width-200)/2, 210, 200, 20);
				g.setColor(Color.BLACK);
				drawStringCentered(g,"Quit",230);
				//g.drawString("Credits",(gameSize.width-120)/2+2,198);
		} else
			//SUBMENUS
		if (activeSelection.get(0) == 1) {
			g.setFont(font.deriveFont(Font.PLAIN,20));
			String howToPlay = "Controls:\n"
					+ "Left/Right  Move left/right\n"
					+ "Up/Down     Weapon up/down\n"
					+ "Strg/Ctrl   Fire\n"
					+ "Alt         Jump\n"
					+ "Space       Change Weapon\n"
					+ "P           Pause\n"
					+ "5           Insert Coin\n"
					+ "";
			String[] htp = howToPlay.split("\n");
			for (int i = 0; i < htp.length; i++) {
				g.drawString(htp[i], 10, 10+g.getFontMetrics().getHeight()*(i+1));
			}
		}
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
			case 38: //Up
				if (activeSelection.size() == 1) {
					activeSelection.set(0,(activeSelection.get(0)+3)%4);
				}
				break;
			case 40: //Down
				if (activeSelection.size() == 1) {
					activeSelection.set(0,(activeSelection.get(0)+1)%4);
				}
				break;
			case '\n': case 17: case 16: //Enter/Ctrl/Shift
				if (activeSelection.get(0) == 0) {
					start();
					break;
				} else if (activeSelection.get(0) == 3) {
					close();
					break;
				}
				if (activeSelection.size() == 1) {
					activeSelection.add(0);
				}
				break;
			case 27: case 18: case 'Y': //ESC/Alt/Y
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
