package de.hshannover.pp.slapemhard.objects;

import java.awt.Dimension;
//import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.*;

import javax.imageio.ImageIO;

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
		BULLET,
		ROCKET,
		GRENADE
	};
	private static BufferedImage rocket;
	private Dimension size;
	private BufferedImage image;
	private int destruction;
	private int range;
	private int speed;
	private boolean usesGravity;
	public BulletType(BulletName name) {
		switch (name) {
			case BULLET:
				this.size = new Dimension(2,1);
				this.destruction = 2;
				this.range = 1;
				this.speed = 100;
				break;
			case ROCKET:
				this.size = new Dimension(10,7);
				this.destruction = 50;
				this.range = 10;
				this.speed = 80;
				this.usesGravity = true;
				try {
					this.image = ImageIO.read(new File("resources/images/rocket.png"));
				} catch (IOException e) {}
				break;
			case GRENADE:
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
}
