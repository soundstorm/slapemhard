package de.hshannover.pp.slapemhard.objects;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Person extends CollisionObject {
	@Deprecated
	private BufferedImage image;// TODO Replace with animation Frames
	private Weapon weapon;
	private int health;
	private boolean heading;
	private int weaponAngle;
	
	public Person(int health, Rectangle size) {
		super(size);
		this.health = health;
	}
	public boolean isAlive() {
		return health > 0;
	}
	public void reduceHealth(int damage) {
		health -= damage;
		if (health < 0)
			health = 0;
	}
	public boolean[] move(int x, int y, ArrayList<CollisionObject> collisions) {
		if (x != 0) {
			heading = x<0;
			//Change heading of person
		}
		boolean collision[] = super.collides(collisions, x, -y);
		if (!collision[0]) {
			super.setPos(super.getPosition().x + x,
						 super.getPosition().y);
		}
		if (!collision[1]) {
			super.setPos(super.getPosition().x,
						 super.getPosition().y - y);
		}
		return collision;
	}
	public void shoot() {
		weapon.fire(heading?weaponAngle-180:weaponAngle, new Dimension(super.getPosition().x,super.getPosition().y));
	}
}
