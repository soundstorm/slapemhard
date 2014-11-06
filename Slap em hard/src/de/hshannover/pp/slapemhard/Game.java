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
import de.hshannover.pp.slapemhard.resources.Resource;
import de.hshannover.pp.slapemhard.threads.MoveThread;

public class Game {
	private static Player me;
	private Font font;
	Level activeLevel;
	private Frame frame;
	private double scale;
	private Dimension gameSize;
	
	public Game(Frame frame, Dimension gameSize, double scale) {
		this.frame = frame;
		this.scale = scale;
		this.gameSize = gameSize;
		Resource r = new Resource();
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, r.getInputStream("fonts>pixelated.ttf"));
		} catch (IOException|FontFormatException e) {
		}
		//Choose Player
		me = new Player(this, 1);

		me.addWeapon(new BulletType(BulletType.BulletName.ROCKETLAUNCHER));
		//me.addWeapon(new BulletType(BulletType.BulletName.BULLET));
		//Start game:
		activeLevel = new Level(this, 1);

		me = new Player(this, 100);
		me.addWeapon(new BulletType(BulletType.BulletName.ROCKETLAUNCHER));
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
		return activeLevel.getEnemies();
	}

	public ArrayList<Bullet> getBullets() {
		return activeLevel.getBullets();
	}

	public ArrayList<CollisionObject> getCollisionObjects() {
		return activeLevel.getCollisionObjects();
	}

	public MoveThread getMoveThread() {
		return activeLevel.getMoveThread();
	}

	public Player getPlayer() {
		return me;
	}
	
	public Dimension getBounds() {
		return activeLevel.getBounds();
	}
	
	public double getScale() {
		return scale;
	}
}
