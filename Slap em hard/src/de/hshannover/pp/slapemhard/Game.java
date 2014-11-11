package de.hshannover.pp.slapemhard;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.io.IOException;
import java.util.ArrayList;

import de.hshannover.pp.slapemhard.objects.Bullet;
import de.hshannover.pp.slapemhard.objects.BulletType;
import de.hshannover.pp.slapemhard.objects.CollisionObject;
import de.hshannover.pp.slapemhard.objects.Person;
import de.hshannover.pp.slapemhard.objects.Player;
import de.hshannover.pp.slapemhard.objects.PowerUp;
import de.hshannover.pp.slapemhard.resources.Resource;
import de.hshannover.pp.slapemhard.threads.MoveThread;

public class Game implements Runnable {
	private static Player me;
	private int points;
	private int coins;
	private Font font;
	private int activeLevel = 1;
	private Level level;
	private double scale;
	private Dimension gameSize;
	private Menu menu;
	private int character;
	private Thread thread;
	private static ArrayList<Person> characters = new ArrayList<Person>();
		
	public Game(Menu menu) {
		this.menu = menu;
		this.gameSize = menu.getGameSize();
		this.scale = menu.getScale();
		Resource r = new Resource();
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
	}
	
	public synchronized void start(){
		if(level != null){
			System.out.println("Levelcontrol Thread alread running");
			return;
		}
		level = new Level(this, activeLevel);
		thread = new Thread(this, "Levelcontrol Thread");
		thread.start();
		menu.useCredit();
	}
	
	@Override
	public void run() {
		while (level != null) {
			System.out.println("Starting level "+activeLevel);
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
				}
			} else if (level.done()) {
				activeLevel++;
				level = new Level(this, activeLevel);
				//show store
			} else { //Game has been quit
				level = null;
			}
		}
	}
	
	public void render(Graphics g) {
		//Background
		g.setColor(Color.RED);
		g.fillRect(0,0,gameSize.width,gameSize.height);
		//Header
		g.setColor(Color.WHITE);
		g.setFont(font.deriveFont(Font.PLAIN,24));
		if (menu.getCredits() == 0 && (System.currentTimeMillis()/500)%2 == 0) {
			drawStringCentered(g,"INSERT COIN",24);
		}
		g.setFont(font.deriveFont(Font.PLAIN,8));
		g.drawString("Credits: "+menu.getCredits(), 10, 16);
		g.setFont(font.deriveFont(Font.PLAIN,16));
		drawStringCentered(g,"Choose Character",70);
		drawStringCentered(g,"^",-120+character*80, 150);
		drawStringCentered(g,"Andre",-120, 160);
		drawStringCentered(g,"Luca",-40, 160);
		drawStringCentered(g,"Patrick",40, 160);
		drawStringCentered(g,"Steffen",120, 160);
		for (int i = 0; i < 4; i++)
			characters.get(i).render(g,gameSize.width/2-128+i*80,80);
	}
	private void drawStringCentered(Graphics g, String string, int y) {
		drawStringCentered(g,string,0,y);
	}
	private void drawStringCentered(Graphics g, String string, int relX, int y) {
		g.drawString(string, (gameSize.width-g.getFontMetrics().stringWidth(string))/2+relX, y);
	}
	
	public void keyEvent(int keyCode) {
		switch (keyCode) {
			case 37: //Left
				characters.get(character).setWalking(false);
				character = (character+3)%4;
				characters.get(character).setWalking(true);
				break;
			case 39: //Right
				characters.get(character).setWalking(false);
				character = (character+1)%4;
				characters.get(character).setWalking(true);
				break;
			case 27:
				//return to main screen
				synchronized(menu) {
					menu.notify();
				}
				break;
			case '\n': case '1':
				if (menu.getCredits() > 0) {
					me = new Player(this, character);
					//TODO switch to HANDGUN
					me.addWeapon(new BulletType(BulletType.BulletName.ROCKETLAUNCHER));
					me.addWeapon(new BulletType(BulletType.BulletName.MACHINEGUN));
					me.addWeapon(new BulletType(BulletType.BulletName.HANDGUN));
					start();
				}
				break;
		}
	}
	
	public Level getLevel() {
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
	
	public Dimension getGameSize() {
		return gameSize;
	}
	
	public ArrayList<Person> getEnemies() {
		return level.getEnemies();
	}

	public ArrayList<Bullet> getBullets() {
		return level.getBullets();
	}

	public ArrayList<CollisionObject> getCollisionObjects() {
		return level.getCollisionObjects();
	}

	public ArrayList<PowerUp> getPowerUps() {
		return level.getPowerUps();
	}

	public MoveThread getMoveThread() {
		return level.getMoveThread();
	}

	public Player getPlayer() {
		return me;
	}
	
	public Dimension getBounds() {
		return level.getBounds();
	}
	
	public double getScale() {
		return scale;
	}
}
