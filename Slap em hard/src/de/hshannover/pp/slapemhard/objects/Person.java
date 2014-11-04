package de.hshannover.pp.slapemhard.objects;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import de.hshannover.pp.slapemhard.images.BufferedImageLoader;
import de.hshannover.pp.slapemhard.images.SpriteSheet;

public class Person extends CollisionObject {
	@Deprecated
	private BufferedImage image;// TODO Replace with animation Frames
	private Weapon weapon;
	private int health;
	protected boolean heading;
	private boolean isPlayer;
	private boolean walking;
	private boolean jumping;
	private SpriteSheet animation;
	private int animationFrame;
	
	public Person(int health, Rectangle size) {
		this(health, size, false);
	}
	public Person(int health, Rectangle size, boolean isPlayer) {
		super(size);
		this.health = health;
		this.isPlayer = isPlayer;
		BufferedImageLoader bL = new BufferedImageLoader();
		animation = new SpriteSheet(bL.getImage("images>persons>luca>person.png"),16,56);
	}
	public boolean isAlive() {
		return health > 0;
	}
	public void reduceHealth(int damage) {
		health -= damage;
		if (health < 0)
			health = 0;
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
	public void setWeaponAngle(boolean up) {
		if (heading && ((up && weapon.getAngle() < 90) | (up && weapon.getAngle() > 90)))
			weapon.setAngle(heading?weapon.getAngle()+1:weapon.getAngle()-1);
		if (heading && ((up && weapon.getAngle() > 270) | (up && weapon.getAngle() < 270)))
			weapon.setAngle(heading?weapon.getAngle()-1:weapon.getAngle()+1);
	}
	public Weapon getWeapon() {
		return weapon;
	}
	public void shoot() {
		weapon.fire(new Dimension(super.getPosition().x,super.getPosition().y));
	}
	public void setWeapon(Weapon weapon) {
		this.weapon = weapon;
	}
	public void fire() {
		weapon.fire(new Dimension(super.getPosition().x+22,super.getPosition().y+7));
	}
	@Override
	public void render (Graphics g) {
		//draw person
		//TODO replace with animation frames
		if (jumping) {
			g.drawImage(animation.getTile(2, (heading?7:0)), super.getPosition().x, super.getPosition().y, super.getPosition().width, super.getPosition().height, null);
		} else if (walking) {
			g.drawImage(animation.getTile(1, (heading?7-animationFrame/2:animationFrame/2)), super.getPosition().x, super.getPosition().y, super.getPosition().width, super.getPosition().height, null);
			animationFrame = (animationFrame+1)%8;
		} else {
			g.drawImage(animation.getTile(0, (heading?7:0)), super.getPosition().x, super.getPosition().y, super.getPosition().width, super.getPosition().height, null);
		}
		//draw weapon:
		
	}
	public BufferedImage getImage() {
		return image;
		//TODO replace with animation frame
	}
}
