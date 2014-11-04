package de.hshannover.pp.slapemhard.objects;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.imageio.ImageIO;

import de.hshannover.pp.slapemhard.*;

public class Player extends Person {
	ArrayList<Weapon> weapons = new ArrayList<Weapon>();
	SlapEmHard game;
	private int activeWeapon;
	//private static BufferedImage image;// = new BufferedImage(40,60,BufferedImage.TYPE_INT_ARGB);
	public Player(SlapEmHard game, int health) {
		super(health, new Rectangle(50,10,16,52), false);
		this.game = game;
		/*try {
			image = ImageIO.read(new File("resources>images>persons>luca>full.png"));
		} catch (IOException e) {}
		*/
	}
	/*
	@Override
	public void render(Graphics g) {
		//TODO let all be rendered by superclass Person
		//TODO invert person right
		//draw body
		g.drawImage(image, super.getPosition().x, super.getPosition().y-8, image.getWidth()*(heading?-1:1), image.getHeight(), null);
		//draw arm
		
		//draw legs
		
		//draw weapon
	}
	*/
	
	public void addWeapon(BulletType type) {
		weapons.add(new Weapon(game, type, true));
		//activate weapon immediately
		activeWeapon = weapons.size()-1;
		super.setWeapon(weapons.get(activeWeapon));
	}
	public void changeWapon() {
		if (weapons.size() < 2) return;
		activeWeapon = (activeWeapon+1)%weapons.size();
		super.setWeapon(weapons.get(activeWeapon));
		super.getWeapon().setAngle(0);
	}
}
