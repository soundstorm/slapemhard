package de.hshannover.pp.slapemhard;

import java.awt.Dimension;
//import java.awt.Image;
import java.awt.image.BufferedImage;

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
	private Dimension size;
	private BufferedImage image;
	private int destruction;
	private int range;
	public BulletType(BulletName name) {
		switch (name) {
			case BULLET:
				this.size = new Dimension(2,1);
				this.destruction = 2;
				this.range = 1;
				break;
			case ROCKET:
				this.size = new Dimension(20,10);
				this.destruction = 50;
				this.range = 10;
				break;
			case GRENADE:
				this.size = new Dimension(15,20);
				this.destruction = 40;
				this.range = 30;
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
}
