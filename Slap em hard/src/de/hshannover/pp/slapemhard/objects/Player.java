package de.hshannover.pp.slapemhard.objects;

import java.awt.Rectangle;
import java.util.*;

import de.hshannover.pp.slapemhard.*;

public class Player extends Person {
	ArrayList<Weapon> weapons = new ArrayList<Weapon>();
	Game game;
	private int activeWeapon;
	private int lives = 3;
	private boolean invincable;
	private long invincableTime;
	
	public Player(Game game, int health) {
		super(game, health, new Rectangle(0,0,16,52), Person.PersonName.LUCA, false);
		this.game = game;
	}
	
	public int getLives() {
		return lives;
	}
	
	@Override
	public void reduceHealth(int damage) {
		if (!invincable | super.outOfWindow()[3])
			super.reduceHealth(damage);
		synchronized (game) {
			if (!isAlive())
				game.notify();
		}
	}
	
	@Override
	public boolean[] move(int x, int y, ArrayList<CollisionObject> collisions) {
		boolean collision[] = super.move(x, y, collisions);
		
		for (int i = 0; i < game.getPowerUps().size(); i++) {
			if (game.getPowerUps().get(i).getPosition().intersects(super.getPosition())) {
				game.getPowerUps().get(i).collect();
				game.getPowerUps().remove(i);
				i--;
			}
		}
		return collision;
	}
	
	public void setLives(int lives) {
		this.lives = lives;
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

	public void restoreAmmo() {
		for (Weapon w : weapons) {
			w.restoreAmmo();
		}
	}

	public void setInvincable() {
		if (invincable) {
			invincableTime = System.currentTimeMillis();
			return;
		}
		invincable = true;
		(new Thread() {
			@Override
			public void start() {
				invincableTime = System.currentTimeMillis();
				super.start();
			}
			public void run() {
				while (invincableTime+10000 > System.currentTimeMillis()) {
					Thread.yield();
				}
				invincable = false;
			}
		}).start();
	}
	public boolean isInvincable() {
		return invincable;
	}
}
