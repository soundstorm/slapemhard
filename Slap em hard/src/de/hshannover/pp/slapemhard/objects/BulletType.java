package de.hshannover.pp.slapemhard.objects;

import java.awt.Dimension;
import java.util.ArrayList;

import de.hshannover.pp.slapemhard.images.BufferedImageLoader;
import de.hshannover.pp.slapemhard.images.SpriteSheet;

/**
  * Objekt für verschiedene Projektile
  *
  *
  * @author
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
	private int range;
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
	public BulletType(BulletName name) {
		//Bullets must be not faster than 70
		switch (name) {
			case HANDGUN:
				this.size = new Dimension(2,1);
				this.destruction = 5;
				this.range = 1;
				this.ammo = 60;
				this.speed = 60;
				this.tiles = 4;
				this.explosion = new SpriteSheet(bL.getImage("images/weapons/handgun/explosion.png"),3,3);
				this.weapon = new SpriteSheet(bL.getImage("images/weapons/handgun/weapon.png"),48,60);
				this.bullet = new SpriteSheet(bL.getImage("images/weapons/handgun/bullet.png"),2,3);
				this.offsets.add(new Dimension(17,23));	//Facing down
				this.offsets.add(new Dimension(20,12));	//Facing straight
				this.offsets.add(new Dimension(15,3));	//Facing up
				
				break;
			case ROCKETLAUNCHER:
				this.size = new Dimension(10,7);
				this.destruction = 20;
				this.range = 10;
				this.speed = 40;
				this.tiles = 24;
				this.ammo = 20;
				this.usesGravity = true;
				this.explosion = new SpriteSheet(bL.getImage("images/weapons/rocketlauncher/explosion.png"),64,64);
				this.weapon = new SpriteSheet(bL.getImage("images/weapons/rocketlauncher/weapon.png"),48,60);
				this.bullet = new SpriteSheet(bL.getImage("images/weapons/rocketlauncher/bullet.png"),10,11);
				this.offsets.add(new Dimension(17,17));	//Facing down
				this.offsets.add(new Dimension(16,6));	//Facing straight
				this.offsets.add(new Dimension(11,0));	//Facing up
				break;
			case MACHINEGUN:
				this.size = new Dimension(2,1);
				this.destruction = 3;
				this.precision = 10;
				this.range = 30;
				this.speed = 70;
				this.ammo = 240;
				this.tiles = 4;
				this.automatic = true;
				this.explosion = new SpriteSheet(bL.getImage("images/weapons/machinegun/explosion.png"),3,3);
				this.weapon = new SpriteSheet(bL.getImage("images/weapons/machinegun/weapon.png"),48,60);
				this.bullet = new SpriteSheet(bL.getImage("images/weapons/machinegun/bullet.png"),2,3);
				this.offsets.add(new Dimension(18,24));	//Facing down
				this.offsets.add(new Dimension(22,13));	//Facing straight
				this.offsets.add(new Dimension(15,4));	//Facing up
				break;
		}
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
	public int getRange() {
		return range;
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
}
