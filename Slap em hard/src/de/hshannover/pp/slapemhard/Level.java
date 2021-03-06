package de.hshannover.pp.slapemhard;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.logging.Logger;

import de.hshannover.pp.slapemhard.images.BufferedImageLoader;
import de.hshannover.pp.slapemhard.images.BufferedImageReference;
import de.hshannover.pp.slapemhard.images.SpriteSheet;
import de.hshannover.pp.slapemhard.objects.Bullet;
import de.hshannover.pp.slapemhard.objects.BulletType;
import de.hshannover.pp.slapemhard.objects.ObjectPrototype;
import de.hshannover.pp.slapemhard.objects.Person;
import de.hshannover.pp.slapemhard.objects.Player;
import de.hshannover.pp.slapemhard.objects.PowerUp;
import de.hshannover.pp.slapemhard.objects.Weapon;
import de.hshannover.pp.slapemhard.threads.DrawThread;

/**
 * @author	Patrick Defayay<br />
 * 			Andre Schmidt<br />
 * 			Steffen Schulz<br />
 * 			Luca Zimmermann
 *
 */

public class Level {
	private static final Logger log = Logger.getLogger(Level.class.getName());
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
	//private long startTime;
	private int timeElapsed;
	private Rectangle targetArea;
	private boolean completed;
	private boolean created;
	private boolean debug = false;// = true;
	private boolean spacePressed;
	private static LevelManager levelManager = new LevelManager();
	private static SpriteSheet lives = new SpriteSheet((new BufferedImageLoader()).getImage("images/lives.png"),11,11);
	
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
		//startTime = System.currentTimeMillis();
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
	
	public int getRemainingTime() {
		//long tR = levelTime*100-(System.currentTimeMillis()-startTime)/10;
		if (timeUp()) {
			synchronized(game) {
				game.notify();
			}
			return 0;
		}
		return levelTime*100-timeElapsed*100/DrawThread.amountOfTicks;
		//return ( > 0)?tR:0;
	}
	
	public boolean timeUp() {
		return timeElapsed >= levelTime*DrawThread.amountOfTicks;
		//return System.currentTimeMillis() > startTime+levelTime*1000;
	}
	
	public void render(Graphics g) {
		Rectangle activePosition = this.getPlayer();// = new Dimension(level.getPlayer().x,level.getPlayer().y);
		
		int xoffset = this.getPlayer().x-100;
		if (xoffset < 0) {
			xoffset = 0;
		} else if (xoffset > this.getWidth()-SlapEmHard.WIDTH) {
			xoffset = this.getWidth()-SlapEmHard.WIDTH;
		}
		
		for (BufferedImage bI : this.getBackgroundImages()) {
			g.drawImage(bI, -xoffset*(bI.getWidth()-SlapEmHard.WIDTH)/(this.getWidth()-SlapEmHard.WIDTH), 0, null);
		}
		Graphics2D g2d = (Graphics2D)g;
		//Move to active clip
		g2d.translate(-xoffset, 0);
		g.drawImage(this.getLandscapeImage(),0,0,null);
		
		
		for (int i = 0; i < this.getBullets().size(); i++) {
			try {
				Bullet obj =  this.getBullets().get(i);
				obj.render(g);
			} catch (Exception e) {
				System.out.println("Cant render Bullet");
			}
		}
		
		for (int i = 0; i < this.getPowerUps().size(); i++) {
			try {
				PowerUp obj =  this.getPowerUps().get(i);
				obj.render(g);
			} catch (Exception e) {
				System.out.println("Cant render PowerUp");
			}
		}
		
		for (int i = 0; i < this.getEnemies().size(); i++) {
			try {
				Person obj =  this.getEnemies().get(i);
				//if (obj.getPosition().x > xoffset-100 | obj.getPosition().x+obj.getPosition().width < xoffset+220)
					obj.render(g);
			} catch (Exception e) {
				System.out.println("Cant render Enemy");
			}
		}
		//Render at position previously determined
		//Prevents shaking, if Player moved in the meantime of rendering
		this.getPlayer().render(g,activePosition.x,activePosition.y);

		//
		g2d.translate(xoffset, 0);
		
		for (BufferedImage fI : this.getForegroundImages()) {
			g.drawImage(fI, -xoffset*(fI.getWidth()-SlapEmHard.WIDTH)/(this.getWidth()-SlapEmHard.WIDTH), 0, null);
		}
		if (debug) {
			g.setColor(new Color(255, 0, 0, 100));
			for (Rectangle ro : this.getCollisionObjects()) {
				g.fillRect(ro.x-xoffset, ro.y, ro.width, ro.height);
			}
		}
		
		
		//Draw HUD
		g.setColor(new Color(255, 255, 255, 127));
		g.fillRect(5,5,50,33);
		g.setColor(Color.BLACK);
		g.setFont(this.getFont().deriveFont(Font.PLAIN,5));
		g.drawString("A", 10, 13);
		drawIntAligned(g, this.getPlayer().getWeapon().getAmmo(), 50, 13);
		g.drawString("C", 10, 20);
		drawIntAligned(g, game.getCoins(), 50, 20);
		g.drawString("P", 10, 27);
		drawIntAligned(g, game.getPoints(), 50, 27);
		g.drawString("T", 10, 34);
		drawIntAligned(g, (int)(getRemainingTime()/100), 50, 34);
		for (int i=0; i < getPlayer().getLives(); i++) {
			if (i != getPlayer().getLives()-1) {
				g.drawImage(lives.getTile(9), 60+i*12, 10, null);
			} else {
				if (this.getPlayer().isInvincible()) {
					g.drawImage(lives.getTile(10), 60+i*12, 10, null);
				} else {
					g.drawImage(lives.getTile(9*this.getPlayer().getHealth()/this.getPlayer().getMaxHealth()), 60+i*12, 10, null);
				}
			}
		}
	}
	
	private void drawIntAligned(Graphics g, int i, int x, int y) {
		drawStringAligned(g, ""+i, x, y);
	}
	
	private void drawStringAligned(Graphics g, String s, int x, int y) {
		g.drawString(s, x-(""+s).length()*5, y);
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

	public void tick() {
		if (!this.timeUp())
			timeElapsed++;
		game.getPlayer().move();
		for (int i = 0; i < this.getBullets().size(); i++) {
			try {
				Bullet obj = this.getBullets().get(i);
				obj.move();
				if (obj.isExploded()) {
					this.getBullets().remove(obj);
					i--;
				}
			} catch (Exception e) {
				log.warning("Cant modify Bullet:\n"+e.toString());
			}
		}
		for (int i = 0; i < this.getEnemies().size(); i++) {
			try {
				Person obj = this.getEnemies().get(i);
				obj.move();
				if (!obj.isAlive()) {
					//obj.stop();
					game.addPoints(obj.getPower()*40);
					this.getPowerUps().add(new PowerUp(game,new Dimension(obj.x,obj.y+30),1));
					this.getEnemies().remove(obj);
					i--;
					//continue;
				}
			} catch (Exception e) {
				log.warning("Cant modify Bullet:\n"+e.toString());
			}
		}
	}
	
	public void keyEvent(int keyCode, boolean type) {
		switch (keyCode) {
			case KeyMap.ESCAPE:
				synchronized (getGame()) {
					getGame().notify();
				}
			case KeyMap.BUTTON3:
				if (!spacePressed && type) {
					boolean collision[] = getPlayer().collides(getCollisionObjects(),0,1);	//Check if on floor
					if (collision[1]) {														//Only Jump, when on floor
						getPlayer().setJump(true);
					}
				} else if (!type) {
					getPlayer().setJump(false);
				}
				spacePressed = type;
				break;
			case KeyMap.BUTTON2:
				getPlayer().setFire(type);
				break;
			case KeyMap.BUTTON1:
				if (type)
					getPlayer().changeWapon();
				break;
			case KeyMap.LEFT:
				getPlayer().setLeft(type);
				if (type)
					getPlayer().setRight(false);
				break;
			case KeyMap.RIGHT:
				getPlayer().setRight(type);
				if (type)
					getPlayer().setLeft(false);
				break;
			case KeyMap.UP:
				getPlayer().getWeapon().setAngle(type?1:0);
				break;
			case KeyMap.DOWN:
				getPlayer().getWeapon().setAngle(type?-1:0);
				break;
			default:
				System.out.println("KeyCode: "+keyCode);
		}
	}
}
