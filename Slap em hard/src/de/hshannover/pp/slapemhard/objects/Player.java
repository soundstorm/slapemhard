package de.hshannover.pp.slapemhard.objects;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.imageio.ImageIO;

import de.hshannover.pp.slapemhard.*;

public class Player extends Person {
	ArrayList<Weapon> weapons;
	SlapEmHard game;
	private static BufferedImage image = new BufferedImage(40,60,BufferedImage.TYPE_INT_ARGB);
	public Player(SlapEmHard game, int health) {
		super(health, new Rectangle(50,10,60,37), false);
		this.game = game;
		try {
			image = ImageIO.read(new File("resources/images/nyan.png"));
		} catch (IOException e) {}
	}
	@Override	
	public BufferedImage getImage() {
		return image;
	}

	public void setWeapon(BulletType type) {
		super.setWeapon(new Weapon(game, type, true));
	}
	
}
