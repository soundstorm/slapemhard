package de.hshannover.pp.slapemhard.objects;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.hshannover.pp.slapemhard.Game;
import de.hshannover.pp.slapemhard.images.BufferedImageLoader;
import de.hshannover.pp.slapemhard.images.SpriteSheet;
/**
 * A person is an actor, holding a weapon. A Person can move, shoot and may die.
 * @see de.hshannover.pp.slapemhard.objects.CollisionObject
 * @author SoundStorm
 *
 */
public class Person extends CollisionObject {
	private static final Logger log = Logger.getLogger(Person.class.getName());
	
	private Weapon weapon;
	private int maxHealth,health;
	protected boolean heading;
	//private boolean isPlayer;
	private boolean walking;
	private boolean jumping;
	private SpriteSheet animation;
	private SpriteSheet arm;
	private int animationFrame;
	private boolean autonomous;
	private Thread autonomic;

	private double rate;
	public enum PersonName {
		ANDRE,
		LUCA,
		PATRICK,
		STEFFEN,
		ENEMY0,
		ENEMY1,
		ENEMY2,
		ENEMY3,
		ENEMY4,
		ENEMY5,
		ENEMY6,
		ENEMY7,
		ENEMY8,
		ENEMY9
	}
	public Person(Game game, int health, Dimension position, PersonName name) {
		this(game, health, position, name, false);
	}
	public Person(Game game, int health, Rectangle size, PersonName name, boolean isPlayer) {
		this(game,health,new Dimension(size.x,size.y),name,isPlayer);
	}
	public Person(Game game, int health, Dimension position, PersonName name, boolean isPlayer) {
		super(game,new Rectangle(position.width,position.height,16,52));
		this.health = this.maxHealth = health;
		//this.isPlayer = isPlayer;
		BufferedImageLoader bL = new BufferedImageLoader();
		switch (name) {
			case ANDRE:
				
			case LUCA:
				animation = new SpriteSheet(bL.getImage("images/persons/luca/person.png"),16,56);
				arm = new SpriteSheet(bL.getImage("images/persons/luca/arm.png"),38,60);
				break;
			case PATRICK:
			case STEFFEN:
				animation = new SpriteSheet(bL.getImage("images/persons/patrick/person.png"),16,56);
				arm = new SpriteSheet(bL.getImage("images/persons/patrick/arm.png"),38,60);
				break;
				
			case ENEMY0:
				
			default:
				
		}
	}
	/**
	 * Returns if Person is alive (health > 0)
	 * @return if Person is alive (health > 0)
	 */
	public boolean isAlive() {
		return health > 0;
	}
	/**
	 * Reduces the active health by the given amount
	 * @param damage Damage to apply
	 */
	public void reduceHealth(int damage) {
		health -= damage;
		if (health <= 0) {
			health = 0;
			stop();
		}
	}
	/**
	 * Restores the maximum health
	 */
	public void restoreHealth() {
		health = maxHealth;
	}
	/**
	 * Returns actual health
	 * @return actual health
	 */
	public int getHealth() {
		return health;
	}
	/**
	 * Returns maximum health
	 * @return maximum health
	 */
	public int getMaxHealth() {
		return maxHealth;
	}
	/**
	 * Sets the maximum health, that can be gained or restored by e.g. a medipack.
	 * @param maxHealth Maximum health
	 */
	public void setMaxHealth(int maxHealth) {
		this.maxHealth = maxHealth;
	}
	/**
	 * Sets the animation accordingly to the current movement
	 * @param jumping Whether the person is currently walking (moving in x-axis)
	 * or not
	 */
	public void setWalking(boolean walking) {
		if (this.walking == walking) return;
		this.walking = walking;
		animationFrame = 0;
	}
	/**
	 * Sets the animation accordingly to the current movement
	 * @param jumping Whether the person is currently jumping/falling (moving in
	 * y-axis) or not
	 */
	public void setJumping(boolean jumping) {
		if (this.jumping == jumping) return;
		this.jumping = jumping;
		animationFrame = 0;
	}
	/**
	 * Move person in the directions. Checks if person touches left or right bound
	 * of map, as well as touching CollisionObjects. Furthermore it checks if the
	 * Person has moved out of bottom of screen, the health will be decreased to
	 * zero (dead).
	 * @see de.hshannover.pp.slapemhard.objects.CollisionObject
	 * @param x Movement in x-axis
	 * @param y Movement in y-axis
	 * @param collisions CollisionObjects to check aginst
	 * @return Two booleans as array containing collision at x- and y-axis (true
	 * if person collides on that axis with any CollisionObject or left/right bounds
	 * of map).
	 */
	public boolean[] move(int x, int y, ArrayList<CollisionObject> collisions) {
		if (x != 0) {
			heading = x<0;
			//Change heading of person
			weapon.setHeading(heading);
		}
		boolean collision[] = super.collides(collisions, x, y);
		boolean[] cwb = this.collidesWithBounds();
		if ((cwb[0] && x < 0) | (cwb[2] && x > 0)) {
			collision[0] = true;
		}
		if (!collision[0]) {
			super.setPosition(super.getPosition().x+x,
							  super.getPosition().y);
		}
		if (!collision[1]) {
			super.setPosition(super.getPosition().x,
							  super.getPosition().y + y);
		}
		if (this.outOfWindow()[3]) {
			//to notify Player that health has been set to zero and player is dead
			reduceHealth(health);
		}
		return collision;
	}
	public void setPower(int power) {
		rate = power/100.0;
	}
	/**
	 * Sets a Person to act autonomously (Enemy, Bot)
	 */
	public void setAutonomous() {
		if (autonomous) return;
		autonomous = true;
		autonomic = new Thread() {
			@Override
			public void run() {
				while (!collides(game.getCollisionObjects(),0,1)[1]) {
					move(0,1,game.getCollisionObjects());
				}
				while (true) {
					boolean direction = Math.random() < 0.5;
					int distance = (int)(Math.random()*100);
					setWalking(true);
					for (int i = 0; i < distance; i++) {
						boolean collision[] = {false, false};
						Rectangle check = new Rectangle(getPosition().x+getPosition().width*(direction?1:-1),getPosition().y+1,getPosition().width,getPosition().height);
						for (CollisionObject collide : game.getCollisionObjects()) {
							if (check.intersects(collide.getPosition())) {
								collision[1] = true;
								break;
							}
						}
						if (!collision[1]) break;
						collision = move((direction?1:-1),0,game.getCollisionObjects());
						if (Math.random() < rate) {
							fire();
						}
						if (collision[0]) break;
						try {
							Thread.sleep(5);
						} catch (InterruptedException e) {
							log.info("Person died and has stopped autonomous behaviour");
							return;
						}
						if (!isAlive()) break;
					}
					setWalking(false);
					try {
						Thread.sleep((int)(Math.random()*200));
					} catch (InterruptedException e) {
						log.info("Person died and has stopped autonomous behaviour");
						return;
					}
				}
			}
		};
		autonomic.start();
	}
	/**
	 * Stops autonimic movement thread
	 */
	public void stop() {
		if (autonomous) {
			try {
				autonomic.interrupt();
			} catch (NullPointerException e) {
				log.log(Level.WARNING,"Autonomous person could not be stopped",e);
			}
		}
	}
	/**
	 * Returns active weapon
	 * @return active weapon
	 */
	public Weapon getWeapon() {
		return weapon;
	}
	/**
	 * Sets the active weapon
	 * @param weapon Weapon to set
	 */
	public void setWeapon(Weapon weapon) {
		this.weapon = weapon;
	}
	/**
	 * Calls the weapons fire() method with the coordinates of this Object
	 * @see de.hshannover.pp.slapemhard.objects.Weapon
	 */
	public void fire() {
		weapon.fire(new Dimension(super.getPosition().x+super.getPosition().width/2,super.getPosition().y), heading);
	}
	/**
	 * Renders the Person (Enemy). Job is processed by {@link #render(Graphics, int, int) with
	 * the specific coordinates of this Object} at time of rendering.
	 * @param g Graphics Object to render to
	 */
	@Override
	public void render (Graphics g) {
		render(g,super.getPosition().x,super.getPosition().y);
	}
	/**
	 * Renders the Person (Enemy/Player) at a specific position.<br />
	 * x and y are used due to shaking if the screen clip is moved to the
	 * active position of Player, Player moved in the meantime and is then
	 * rendered at a different position.<br />
	 * Otherwise they would have to be cached too, because this method
	 * renders body, arm and weapon. If person has moved between any of those
	 * steps of rendering, some parts would be misplaced. Heading is cached inside
	 * this method to avoid rendering of parts on the other side.
	 * @see de.hshannover.pp.slapemhard.threads.DrawLevelThread
	 * @param g Graphics Object to render to
	 * @param x X-Coordinate of Object
	 * @param y Y-Coordinate of Object
	 */
	public void render (Graphics g, int x, int y) {
		boolean heading = this.heading;
		//draw person
		if (jumping) {
			g.drawImage(animation.getTile(2, (heading?7:0)), x, y, super.getPosition().width, super.getPosition().height, null);
		} else if (walking) {
			g.drawImage(animation.getTile(1, (heading?7-animationFrame/2:animationFrame/2)), x, y, super.getPosition().width, super.getPosition().height, null);
			animationFrame = (animationFrame+1)%8;
		} else {
			g.drawImage(animation.getTile(0, (heading?7:0)), x, y, super.getPosition().width, super.getPosition().height, null);
		}
		//draw arm
		if (weapon == null) {
			g.drawImage(arm.getTile(heading?7:0), x-11, y-6, null);
		} else {
			g.drawImage(arm.getTile(heading?5-weapon.getAngle():2+weapon.getAngle()), x-11, y-6,null);
			g.drawImage(weapon.getType().getWeapon().getTile(heading?4-weapon.getAngle():1+weapon.getAngle()), x-16, y-6, null);
		}
		
	}
}
