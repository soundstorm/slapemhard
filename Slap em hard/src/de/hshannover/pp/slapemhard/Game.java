package de.hshannover.pp.slapemhard;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.logging.Logger;

import de.hshannover.pp.slapemhard.images.BufferedImageLoader;
import de.hshannover.pp.slapemhard.objects.*;
import de.hshannover.pp.slapemhard.resources.Resource;
//import de.hshannover.pp.slapemhard.threads.MoveThread;
import de.hshannover.pp.slapemhard.resources.SoundPlayer;

public class Game implements Runnable {
	private static final Logger log = Logger.getLogger(Game.class.getName());
	
	private static Player me;
	private int points;
	private int coins;
	private Font font;
	private int activeLevel = 1;
	private Level level;
	private double scale;
	private Menu menu;
	private int character;
	private Thread thread;
	private Highscore highscore = new Highscore();
	private boolean inputName;

	private boolean store;
	private static int activeChar;
	private static StringBuilder name = new StringBuilder("AAAA");
	private static ArrayList<Person> characters = new ArrayList<Person>();
	private static final Resource r = new Resource();
	
	private int storeItem;
	private final BufferedImageLoader bL = new BufferedImageLoader();
	private final BufferedImage storeBackground = bL.getImage("/images/shop.png");

	private int[] price = {700,1500,1000,500,4000,200};
	private String[] storeItems = {"Handgun","Rocketlauncher","Maschinegun","Medipack","Extra Life","Magazine"};
		
	public Game(Menu menu) {
		this.menu = menu;
		this.scale = menu.getScale();
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, r.getInputStream("fonts/04b.ttf"));
		} catch (IOException|FontFormatException e) {
		}
		if (characters.size() == 0) {
			for (int i = 0; i < 4; i++)
				characters.add(new Person(null,1,new Dimension(),Person.PersonName.values()[i]));
		}
		for (int i = 0; i < 4; i++)
			characters.get(i).setWalking(i==0);
		parseHighscore();
	}
	/**
	 * Reads the serialized {@link Highscore} Object from file
	 */
	private void parseHighscore() {
		Highscore e = null;
		try {
			File file = new File("scores.dat");
			FileInputStream fileIn = new FileInputStream(file);
			ObjectInputStream in = new ObjectInputStream(fileIn);
			e = (Highscore) in.readObject();
			in.close();
			fileIn.close();
			highscore = e;
		} catch(IOException i) {
			log.warning("Could not load saved highscores");
		} catch(ClassNotFoundException c) {
			log.severe("Highscore class was not found");
		}
	}
	/**
	 * Saves a serialized version of the active {@link Highscore} Object
	 */
	private void saveHighscore() {
		try {
			File file = new File("scores.dat");
			if (!file.exists()) file.createNewFile();
			FileOutputStream fileOut = new FileOutputStream(file);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(highscore);
			out.close();
			fileOut.close();
			System.out.println("Saved to "+file.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Starts running the Levels
	 */
	public synchronized void start(){
		if(level != null){
			System.out.println("Levelcontrol Thread alread running");
			return;
		}
		points = 0;
		coins = 0;
		activeLevel = 1;
		level = new Level(this, activeLevel);
		thread = new Thread(this, "Levelcontrol Thread");
		thread.start();
		menu.useCredit();
	}
	/**
	 * Runs the levels as long as the {@link Player} is alive. Restarts automatically on death
	 */
	@Override
	public void run() {
		menu.getBgm().stopAudio();
		while (level != null) {
			log.info("Starting level "+activeLevel);
			level.start();
			synchronized (this) {
				try {
					this.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			level.stop();
			if (!me.isAlive() | level.timeUp()) {
				me.setLives(me.getLives()-1);
				level = null;
				if (me.getLives() > 0) {
					me.restoreHealth();
					me.restoreAmmo();
					level = new Level(this, activeLevel);
				} else if (points > highscore.getScore(7)){
					inputName = true;
				}
			} else if (level.isCompleted()) {
				points += level.getRemainingTime()/10;
				activeLevel++;
				openStore();
				while (store)
					Thread.yield();
			} else { //Game has been quit
				level = null;
			}
		}
		menu.getBgm().play();
	}
	
	public void render(Graphics g) {
		if (getLevel() != null) {
			level.render(g);
			return;
		}
		
		//Background
		if (!store)
			g.drawImage(menu.getBackground(), 0, 0, null);
		g.setColor(Color.BLACK);
		g.setFont(font.deriveFont(Font.PLAIN,16));
		if (inputName) {
			for (int i = 0; i < 4; i++) {
				g.drawString(""+name.charAt(i),40+40*i,135);
			}
			g.drawString("OKAY",240,135);
			if ((System.currentTimeMillis()/200)%2 == 0 && activeChar != 4)
				g.drawString("_",40+40*activeChar,140);
			if (activeChar < 4) {
				g.drawString("/\\",33+40*activeChar,125);
				g.drawString("\\/",33+40*activeChar,150);
			}
			if (activeChar == 4)
				g.drawString("____",240,140);
		} else if (store) {
			g.setFont(font.deriveFont(Font.PLAIN,8));
			g.drawImage(storeBackground, 0, 0, null);
			if (storeItem < 6) {
				if (coins >= price[storeItem]) {
					g.setColor(new Color(0, 255, 0, 127));
				} else {
					g.setColor(new Color(255, 0, 0, 127));
				}
				((Graphics2D)g).setStroke(new BasicStroke(5));
				if (storeItem < 3) {
					if (me.hasWeapon(storeItem)) {
						g.setColor(new Color(127, 127, 127, 127));
					}
				}
				g.drawRect(50+170*(storeItem/3), 40+60*(storeItem%3), 50, 50);
			} else {
				g.setColor(Color.WHITE);
				drawStringCentered(g,"___________",232);
			}
			g.setColor(Color.WHITE);
			drawStringCentered(g,"Leave Store",230);
			g.drawString("Bank: "+coins, 10, 18);
			for (int i = 0; i < 6; i++) {
				drawStringCentered(g,price[i]+" C",(int)(50*(i/3-.5)), 70+60*(i%3));
				drawStringCentered(g,storeItems[i],(int)(50*(i/3-.5)), 90+60*(i%3));
			}
		} else {
			drawStringCentered(g,"Choose Character",77);
			drawStringCentered(g,"^",-120+character*80, 145);
			drawStringCentered(g,"Andre",-120, 150);
			drawStringCentered(g,"Luca",-40, 150);
			drawStringCentered(g,"Patrick",40, 150);
			drawStringCentered(g,"Steffen",120, 150);
			g.drawString("Credits: "+menu.getCredits(), 10, 234);
			if (menu.getCredits() == 0 && (System.currentTimeMillis()/500)%2 == 0) {
				drawStringCentered(g,"INSERT COIN",234);
			}
			for (int i = 0; i < 4; i++)
				characters.get(i).render(g,SlapEmHard.WIDTH/2-128+i*80,80);
			for (int i = 0; i < 8; i++) {
				g.drawString(i+1+".",10+160*(i/4),165+17*(i%4));
				g.drawString(highscore.getName(i),30+160*(i/4),165+17*(i%4));
				g.drawString(""+highscore.getScore(i),80+160*(i/4),165+17*(i%4));
			}
		}
	}
	private void drawStringCentered(Graphics g, String string, int y) {
		drawStringCentered(g,string,0,y);
	}
	private void drawStringCentered(Graphics g, String string, int relX, int y) {
		g.drawString(string, (SlapEmHard.WIDTH-g.getFontMetrics().stringWidth(string))/2+relX, y);
	}
	
	private void openStore() {
		Level tmplevel;
		tmplevel = new Level(this, activeLevel);
		if (tmplevel.isCreated()) {
			store = true;
			level = tmplevel;
			return;
		}
		if (points > highscore.getScore(7))
			inputName = true;
		level = null;
	}
	
	private void leaveStore() {
		store = false;
	}
	
	public void keyEvent(int keyCode) {
		switch (keyCode) {
			case KeyEvent.VK_LEFT:
				if (inputName) {
					activeChar = (activeChar+4)%5;
				} else if (store) {
					if (storeItem < 6) {
						storeItem = (storeItem+3)%6;
					}
				} else {
					characters.get(character).setWalking(false);
					character = (character+3)%4;
					characters.get(character).setWalking(true);
				}
				break;
			case KeyEvent.VK_RIGHT:
				if (inputName) {
					activeChar = (activeChar+1)%5;
				} else if (store) {
					if (storeItem < 6) {
						storeItem = (storeItem+3)%6;
					}
				} else {
					characters.get(character).setWalking(false);
					character = (character+1)%4;
					characters.get(character).setWalking(true);
				}
				break;
			case KeyEvent.VK_UP:
				if (inputName) {
					name.setCharAt(activeChar, (char)((name.charAt(activeChar)-64)%26+65));
				} else if (store) {
					storeItem = (storeItem+6)%7;
				}
				break;
			case KeyEvent.VK_DOWN:
				if (inputName) {
					name.setCharAt(activeChar, (char)((name.charAt(activeChar)-40)%26+65));
				} else if (store) {
					storeItem = (storeItem+1)%7;
				}
				break;
			case KeyEvent.VK_ESCAPE:
				//return to main screen
				if (store)
					leaveStore();
				else 
					synchronized(menu) {
						menu.notify();
					}
				break;
			case KeyEvent.VK_1:
				if (inputName) break;
			case KeyEvent.VK_ENTER:
			case KeyEvent.VK_CONTROL:
			case KeyEvent.VK_SHIFT:
				if (inputName) {
					if (activeChar == 4) {
						highscore.addHighscore(name.toString(), points);
						points = 0;
						inputName = false;
						saveHighscore();
					}
					break;
				} else if (store) {
					if (storeItem == 6) {
						leaveStore();
						break;
					}
					if (coins >= price[storeItem]) {
						switch (storeItem) {
						case 1:
							me.addWeapon(new BulletType(BulletType.BulletName.ROCKETLAUNCHER));
							break;
						case 2:
							me.addWeapon(new BulletType(BulletType.BulletName.MACHINEGUN));
							break;
						case 3:
							me.restoreHealth();break;
						case 4:
							me.setLives(me.getLives()+1);break;
						case 5:
							me.restoreAmmo();break;
						}
						coins -= price[storeItem];
						(new SoundPlayer("sounds/bought.wav",0)).play();
					}
				} else if (menu.getCredits() > 0) {
					me = new Player(this, character);
					me.addWeapon(new BulletType(BulletType.BulletName.ROCKETLAUNCHER));
					me.addWeapon(new BulletType(BulletType.BulletName.MACHINEGUN));
					//me.addWeapon(new BulletType(BulletType.BulletName.HANDGUN));
					start();
				}
				break;
		}
	}
	
	public Level getLevel() {
		if (store)
			return null;
		return level;
	}
	
	public void addCoins(int coins) {
		this.coins += coins;
	}
	public int getCoins() {
		return coins;
	}
	public boolean useCoins(int coins) {
		if (this.coins < coins) {
			return false;
		}
		this.coins -=  coins;
		return true;
	}
	public void addPoints(int points) {
		this.points += points;
	}
	public int getPoints() {
		return points;
	}

	public Font getFont() {
		return font;
	}
	
	public ArrayList<Person> getEnemies() {
		try{return level.getEnemies();}catch(NullPointerException e){return null;}
	}

	public ArrayList<Bullet> getBullets() {
		try{return level.getBullets();}catch(NullPointerException e){return null;}
	}

	public ArrayList<Rectangle> getCollisionObjects() {
		try{return level.getCollisionObjects();}catch(NullPointerException e){return null;}
	}
	
	public ArrayList<Rectangle> getMaliciousObjects() {
		try{return level.getMaliciousObjects();}catch(NullPointerException e){return null;}
	}

	public ArrayList<PowerUp> getPowerUps() {
		try{return level.getPowerUps();}catch(NullPointerException e){return null;}
	}

	/*public MoveThread getMoveThread() {
		return level.getMoveThread();
	}*/

	public Player getPlayer() {
		return me;
	}
	
	public Rectangle getTargetArea() {
		try{return level.getTargetArea();}catch(NullPointerException e){return null;}
	}
	
	public int getWidth() {
		try {return level.getWidth();} catch (NullPointerException e) {
			return 0;
		}
	}
	
	public double getScale() {
		return scale;
	}
	public void tick() {
		if (getLevel() != null) {
			level.tick();
		}
	}
}
