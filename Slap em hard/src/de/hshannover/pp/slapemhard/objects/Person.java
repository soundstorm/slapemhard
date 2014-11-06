package de.hshannover.pp.slapemhard.objects;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import de.hshannover.pp.slapemhard.Game;
import de.hshannover.pp.slapemhard.images.BufferedImageLoader;
import de.hshannover.pp.slapemhard.images.SpriteSheet;

public class Person extends CollisionObject {
	private Weapon weapon;
	private int maxHealth,health;
	protected boolean heading;
	private boolean isPlayer;
	private boolean walking;
	private boolean jumping;
	private SpriteSheet animation;
	private SpriteSheet arm;
	private int animationFrame;
	private boolean armed;
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
	
	public Person(Game game, int health, Rectangle size, PersonName name) {
		this(game, health, size, name, false);
	}
	public Person(Game game, int health, Rectangle size, PersonName name, boolean isPlayer) {
		super(game,size);
		this.health = this.maxHealth = health;
		this.isPlayer = isPlayer;
		BufferedImageLoader bL = new BufferedImageLoader();
		switch (name) {
			case ANDRE:
				
			case LUCA:
				animation = new SpriteSheet(bL.getImage("images>persons>luca>person.png"),16,56);
				arm = new SpriteSheet(bL.getImage("images>persons>luca>arm.png"),38,60);
				break;
			case PATRICK:
				
			case STEFFEN:
				
			case ENEMY0:
				
			default:
				
		}
	}
	public boolean isAlive() {
		return health > 0;
	}
	public void reduceHealth(int damage) {
		health -= damage;
		if (health < 0)
			health = 0;
	}
	public int getHealth() {
		return health;
	}
	public int getMaxHealth() {
		return maxHealth;
	}
	public void setWalking(boolean b) {
		if (walking == b) return;
		walking = b;
		animationFrame = 0;
	}
	public void setJumping(boolean b) {
		if (jumping == b) return;
		jumping = b;
		animationFrame = 0;
	}
	public boolean[] move(int x, int y, ArrayList<CollisionObject> collisions) {
		if (x != 0) {
			heading = x<0;
			//Change heading of person
			weapon.setHeading(heading);
		}
		boolean collision[] = super.collides(collisions, x, -y);
		if (!collision[0]) {
			super.setPosition(super.getPosition().x+x,
							  super.getPosition().y);
		}
		if (!collision[1]) {
			super.setPosition(super.getPosition().x,
							  super.getPosition().y - y);
		}
		return collision;
	}
	/*public void setWeaponAngle(boolean up) {
		if (heading && ((up && weapon.getAngle() < 90) | (up && weapon.getAngle() > 90)))
			weapon.setAngle(heading?weapon.getAngle()+1:weapon.getAngle()-1);
		if (heading && ((up && weapon.getAngle() > 270) | (up && weapon.getAngle() < 270)))
			weapon.setAngle(heading?weapon.getAngle()-1:weapon.getAngle()+1);
	}*/
	public Weapon getWeapon() {
		return weapon;
	}
	public void setWeapon(Weapon weapon) {
		this.weapon = weapon;
		armed = true;
	}
	public void disarm() {
		armed = false;
	}
	public void fire() {
		weapon.fire(new Dimension(super.getPosition().x+22,super.getPosition().y+7));
	}
	@Override
	public void render (Graphics g) {
		int x = super.getPosition().x;
		int y = super.getPosition().y;
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
		if (!armed) {
			g.drawImage(arm.getTile(heading?7:0), x-11, y-6, null);
		} else {
			g.drawImage(arm.getTile(heading?5-weapon.getAngle():2+weapon.getAngle()), x-11, y-6,null);
			g.drawImage(weapon.getType().getWeapon().getTile(heading?4-weapon.getAngle():1+weapon.getAngle()), x-16, y-6, null);
		}
		//draw weapon:
		
	}
}
