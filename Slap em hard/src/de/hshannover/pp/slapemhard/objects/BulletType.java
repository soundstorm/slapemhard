package de.hshannover.pp.slapemhard.objects;

import java.awt.Dimension;
import java.util.ArrayList;

import de.hshannover.pp.slapemhard.images.BufferedImageLoader;
import de.hshannover.pp.slapemhard.images.SpriteSheet;

/**
  * Objekt für verschiedene Projektile
  *
  *@author	Patrick Defayay<br />
 * 			Andre Schmidt<br />
 * 			Steffen Schulz<br />
 * 			Luca Zimmermann
  * @version
  * @param	name		Name des Projektils
  * @param	size		Größe des Projektils
  * @param	image		Bilddatei
  * @param  destruction Zerstörungskraft des Projektils
  * @param	range		Umkreis der Zerstöung
  */
 

public class BulletType {
	public enum BulletName {
		HANDGUN,
		ROCKETLAUNCHER,
		MACHINEGUN
	};
	private static BufferedImageLoader bL = new BufferedImageLoader();
	private Dimension size;
	private int destruction;
	private int speed;
	private int ammo;
	private boolean usesGravity;
	private SpriteSheet explosion;
	private SpriteSheet weapon;
	private SpriteSheet bullet;
	private ArrayList<Dimension> offsets = new ArrayList<Dimension>();
	private int tiles;
	private boolean automatic;
	private int precision;
	private int bulletId;
	private String explosionSound;
	private String shotSound;
	public BulletType(BulletName name) {
		//Bullets must be not faster than 70;
		switch (name) {
			case HANDGUN:
				this.size = new Dimension(2,1);
				this.destruction = 5;
				this.ammo = 80;
				this.speed = 60;
				this.tiles = 4;
				this.explosion = new SpriteSheet(bL.getImage("images/weapons/handgun/explosion.png"),3,3);
				this.weapon = new SpriteSheet(bL.getImage("images/weapons/handgun/weapon.png"),48,60);
				this.bullet = new SpriteSheet(bL.getImage("images/weapons/handgun/bullet.png"),2,3);
				this.shotSound = "images/weapons/handgun/shot.wav";
				this.offsets.add(new Dimension(17,23));	//Facing down
				this.offsets.add(new Dimension(20,12));	//Facing straight
				this.offsets.add(new Dimension(15,3));	//Facing up
				bulletId = 0;
				break;
			case ROCKETLAUNCHER:
				this.size = new Dimension(10,7);
				this.destruction = 20;
				this.speed = 40;
				this.tiles = 24;
				this.ammo = 20;
				this.usesGravity = true;
				this.explosion = new SpriteSheet(bL.getImage("images/weapons/rocketlauncher/explosion.png"),64,64);
				this.weapon = new SpriteSheet(bL.getImage("images/weapons/rocketlauncher/weapon.png"),48,60);
				this.bullet = new SpriteSheet(bL.getImage("images/weapons/rocketlauncher/bullet.png"),10,11);
				this.shotSound = "images/weapons/rocketlauncher/shot.wav";
				this.explosionSound = "images/weapons/rocketlauncher/explosion.wav";
				this.offsets.add(new Dimension(17,17));	//Facing down
				this.offsets.add(new Dimension(16,6));	//Facing straight
				this.offsets.add(new Dimension(11,0));	//Facing up
				bulletId = 1;
				break;
			case MACHINEGUN:
				this.size = new Dimension(2,1);
				this.destruction = 3;
				this.precision = 10;
				this.speed = 70;
				this.ammo = 240;
				this.tiles = 4;
				this.automatic = true;
				this.explosion = new SpriteSheet(bL.getImage("images/weapons/machinegun/explosion.png"),3,3);
				this.weapon = new SpriteSheet(bL.getImage("images/weapons/machinegun/weapon.png"),48,60);
				this.bullet = new SpriteSheet(bL.getImage("images/weapons/machinegun/bullet.png"),2,3);
				this.shotSound = "images/weapons/machinegun/shot.wav";
				this.offsets.add(new Dimension(18,24));	//Facing down
				this.offsets.add(new Dimension(22,13));	//Facing straight
				this.offsets.add(new Dimension(15,4));	//Facing up
				bulletId = 2;
				break;
		}
	}
	public int getId() {
		return bulletId;
	}
	public Dimension getSize() {
		return size;
	}
	public ArrayList<Dimension> getOffsets() {
		return offsets;
	}
	public int getDestruction() {
		return destruction;
	}
	public int getSpeed() {
		return speed;
	}
	public boolean getGravity() {
		return usesGravity;
	}
	public SpriteSheet getBulletImage() {
		return bullet;
	}
	public SpriteSheet getExplosion() {
		return explosion;
	}
	public SpriteSheet getWeapon() {
		return weapon;
	}
	public int getAnimationLength() {
		return tiles;
	}
	public int getAmmo() {
		return ammo;
	}
	public int getPrecision() {
		return precision;
	}
	public boolean isAutomatic() {
		return automatic;
	}
	public String getShotSound() {
		return shotSound;
	}
	public String getExplosionSound() {
		return explosionSound;
	}
}
