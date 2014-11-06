package de.hshannover.pp.slapemhard.objects;

import java.awt.Dimension;
//import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.*;

import javax.imageio.ImageIO;

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
	private static BufferedImage rocket;
	private Dimension size;
	private BufferedImage image;
	private int destruction;
	private int range;
	private int speed;
	private boolean usesGravity;
	private int tileHeight;
	private int tileWidth;
	private SpriteSheet explosion;
	private SpriteSheet weapon;
	private SpriteSheet bullet;
	private int tiles;
	public BulletType(BulletName name) {
		BufferedImageLoader bL = new BufferedImageLoader();
		switch (name) {
			case HANDGUN:
				this.size = new Dimension(2,1);
				this.destruction = 2;
				this.range = 1;
				this.speed = 100;
				break;
			case ROCKETLAUNCHER:
				this.size = new Dimension(10,7);
				this.destruction = 50;
				this.range = 10;
				this.speed = 40;
				this.tileWidth = 64;
				this.tileHeight = 64;
				this.tiles = 24;
				this.usesGravity = true;
				this.image = bL.getImage("images>rocket.png");
				this.explosion = new SpriteSheet(bL.getImage("images>weapons>rocketlauncher>explosion.png"),tileWidth,tileHeight);
				this.weapon = new SpriteSheet(bL.getImage("images>weapons>rocketlauncher>weapon.png"),48,60);
				this.bullet = new SpriteSheet(bL.getImage("images>weapons>rocketlauncher>bullet.png"),10,11);
				break;
			case MACHINEGUN:
				this.size = new Dimension(15,20);
				this.destruction = 40;
				this.range = 30;
				this.speed = 20;
				this.usesGravity = true;
				break;
		}
	}
	public Dimension getSize() {
		return size;
	}
	@Deprecated
	public BufferedImage getImage() {
		return image;
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
}
