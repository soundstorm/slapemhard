package de.hshannover.pp.slapemhard;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Frame;
import java.awt.Rectangle;
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

public class Game extends Thread {
	private static Player me;
	private int points;
	private int coins;
	private Font font;
	private int activeLevel = 1;
	private Level level;
	private Frame frame;
	private double scale;
	private Dimension gameSize;
	
	public Game(Frame frame, Dimension gameSize, double scale) {
		this.frame = frame;
		this.scale = scale;
		this.gameSize = gameSize;
		Resource r = new Resource();
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, r.getInputStream("fonts/pixelated.ttf"));
		} catch (IOException|FontFormatException e) {
		}
		//Choose Player
		me = new Player(this, 100);

		me.addWeapon(new BulletType(BulletType.BulletName.ROCKETLAUNCHER));
		//me.addWeapon(new BulletType(BulletType.BulletName.HANDGUN));
		//Start game:
	}
	
	@Override
	public void run() {
		while (true) {
			System.out.println("Starting level "+activeLevel);
			level = new Level(this, activeLevel);
			level.start();
			synchronized (this) {
				try {
					this.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if (!me.isAlive() | level.timeUp()) {
				getPlayer().setLives(getPlayer().getLives()-1);
				level.stop();
				if (me.getLives() == 0) {
					System.out.println("Game Over!");
					break;
				}
				me.restoreHealth();
				me.restoreAmmo();
			} else if (level.done()) {
				activeLevel++;
				//show store
			}
		}
	}
	
	public void addCoins(int coins) {
		this.coins += coins;
	}

	public Font getFont() {
		return font;
	}
	
	public Frame getFrame() {
		return frame;
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
