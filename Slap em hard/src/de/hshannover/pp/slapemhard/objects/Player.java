package de.hshannover.pp.slapemhard.objects;

import java.awt.Rectangle;
import java.util.*;

import de.hshannover.pp.slapemhard.*;

public class Player extends Person {
	ArrayList<Weapon> weapons = new ArrayList<Weapon>();
	Game game;
	private int activeWeapon;
	private int lives = 3;
	
	public Player(Game game, int health) {
		super(game, health, new Rectangle(0,0,16,52), Person.PersonName.LUCA, false);
		this.game = game;
	}
	
	public int getLives() {
		return lives;
	}
	
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
