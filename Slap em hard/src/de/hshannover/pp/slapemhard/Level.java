package de.hshannover.pp.slapemhard;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import de.hshannover.pp.slapemhard.images.BufferedImageReference;
import de.hshannover.pp.slapemhard.objects.*;

public class Level {
	private ArrayList<Rectangle> collisionObjects = new ArrayList<Rectangle>();
	private ArrayList<Rectangle> maliciousObjects = new ArrayList<Rectangle>();
	private ArrayList<Person> enemies = new ArrayList<Person>();
	private ArrayList<Bullet> bullets = new ArrayList<Bullet>();
	private ArrayList<PowerUp> powerups = new ArrayList<PowerUp>();
	private Game game;
	private int levelTime;
	private BufferedImage landscapeImage;
	private ArrayList<BufferedImage> backgroundImages = new ArrayList<BufferedImage>();
	private ArrayList<BufferedImage> foregroundImages = new ArrayList<BufferedImage>();
	private int width;
	private long startTime;
	private Rectangle targetArea;
	private boolean completed;
	private boolean created;
	private static LevelManager levelManager = new LevelManager();
	
	public Level(Game game, int level) {
		this.game = game;
		if (levelManager.load(level)) {
			levelTime = levelManager.getLevelTime();
			
			for (ObjectPrototype o : levelManager.getCollisionObjects()) {
				collisionObjects.add(o);
			}
			
			for (ObjectPrototype o : levelManager.getMaliciousObjects()) {
				maliciousObjects.add(o);
			}
			
			for (ObjectPrototype o : levelManager.getEnemies()) {
				Person e = new Person(game, o.getHealth(), new Dimension(o.x, o.y), Person.PersonName.values()[o.getLook()]);
				e.setPower(o.getPower());
				e.setWeapon(new Weapon(game, new BulletType(BulletType.BulletName.values()[o.getWeapon()])));
				enemies.add(e);
			}
			
			for (ObjectPrototype o : levelManager.getPowerups()) {
				powerups.add(
					new PowerUp(
						game,
						new Dimension(
								o.x,
								o.y
						),
						o.getType()
					)
				);
			}
			
			for (BufferedImageReference bR : levelManager.getBackgroundImages())
				backgroundImages.add(bR.getImage());
			
			for (BufferedImageReference bR : levelManager.getForegroundImages())
				foregroundImages.add(bR.getImage());
			
			landscapeImage = levelManager.getLandscapeImage().getImage();
			
			targetArea = levelManager.getTargetArea();
			
			game.getPlayer().setPosition(levelManager.getPlayer().x,levelManager.getPlayer().y);
			game.getPlayer().setLeft(false);
			game.getPlayer().setRight(false);
			game.getPlayer().setFire(false);
			game.getPlayer().setJump(false);
			
			width = landscapeImage.getWidth();
			
			created = true;
		}
	}
	
	public boolean isCreated() {
		return created;
	}
	/**
	 * Runs the level
	 */
	public void start() {
		startTime = System.currentTimeMillis();
		for (Person e : enemies) {
			//e.setAutonomous();
			e.init();
		}
	}
	/**
	 * Stops all Threads
	 */
	public void stop() {
		/*System.out.println("Stopping mover");
		for (Person p : game.getEnemies()) {
			p.stop();
		}*/
	}
	/**
	 * Returns if Level is completed
	 * @return if Level is completed
	 */
	public boolean isCompleted() {
		return completed;
	}
	
	public long getRemainingTime() {
		long tR = levelTime*100-(System.currentTimeMillis()-startTime)/10;
		if (tR < 0) {
			synchronized(game) {
				game.notify();
			}
		}
		return (tR > 0)?tR:0;
	}
	
	public boolean timeUp() {
		return System.currentTimeMillis() > startTime+levelTime*1000;
	}
	
	public BufferedImage getLandscapeImage() {
		return landscapeImage;
	}
	
	public ArrayList<Person> getEnemies() {
		return enemies;
	}

	public ArrayList<Bullet> getBullets() {
		return bullets;
	}

	public ArrayList<Rectangle> getCollisionObjects() {
		return collisionObjects;
	}
	
	public ArrayList<Rectangle> getMaliciousObjects() {
		return maliciousObjects;
	}
	
	public ArrayList<PowerUp> getPowerUps() {
		return powerups;
	}

	public Player getPlayer() {
		return game.getPlayer();
	}
	
	public Rectangle getTargetArea() {
		return targetArea;
	}

	public Font getFont() {
		return game.getFont();
	}

	public ArrayList<BufferedImage> getBackgroundImages() {
		return backgroundImages;
	}
	
	public int getWidth() {
		return width;
	}

	public Game getGame() {
		return game;
	}

	public ArrayList<BufferedImage> getForegroundImages() {
		return foregroundImages;
	}
	public void setCompleted() {
		completed = true;
	}
}
