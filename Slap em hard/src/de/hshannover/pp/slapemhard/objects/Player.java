package de.hshannover.pp.slapemhard.objects;

import java.awt.Dimension;
import java.util.*;

import de.hshannover.pp.slapemhard.*;

/**
 * Special type of person. Has lives, can hold more than one weapon and may be
 * invincible.
 * @author SoundStorm
 *
 */
public class Player extends Person {
	ArrayList<Weapon> weapons = new ArrayList<Weapon>();
	Game game;
	private int activeWeapon;
	private int lives = 3;
	private boolean invincible;
	private long invincibleTime;
	private Thread invincibleThread;
	
	public Player(Game game, int person) {
		super(game, 100, new Dimension(), Person.PersonName.values()[person], false);
		this.game = game;
	}
	/**
	 * Returns number of lives
	 * @return number of lives
	 */
	public int getLives() {
		return lives;
	}
	/**
	 * Checks if player is invincible, if not or player is out of window, calls super method.
	 */
	@Override
	public void reduceHealth(int damage) {
		if (!invincible | super.outOfWindow()[3])
			super.reduceHealth(damage);
		synchronized (game) {
			if (!isAlive()) {
				try {
					invincibleThread.interrupt();
				} catch (NullPointerException e) {}
				invincible = false;
				game.notify();
			}
		}
	}
	/**
	 * Moves player by {@link Person#move(int, int, ArrayList) supermethod} and checks if
	 * player is touching any {@link PowerUp PowerUp}. 
	 */
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
	/**
	 * Changes the amount of remaining lives
	 * @param lives number of lives
	 */
	public void setLives(int lives) {
		this.lives = lives;
	}
	/**
	 * Adds a weapon to the players weapons
	 * @param type Type of weapon to add
	 */
	public void addWeapon(BulletType type) {
		weapons.add(new Weapon(game, type, true));
		//activate weapon immediately
		activeWeapon = weapons.size()-1;
		super.setWeapon(weapons.get(activeWeapon));
	}
	/**
	 * Sets active weapon to the next available
	 */
	public void changeWapon() {
		if (weapons.size() < 2) return;
		activeWeapon = (activeWeapon+1)%weapons.size();
		super.setWeapon(weapons.get(activeWeapon));
		super.getWeapon().setAngle(0);
	}
	/**
	 * Restores the (maximum) ammo of all {@link Weapon#restoreAmmo() weapons}.
	 */
	public void restoreAmmo() {
		for (Weapon w : weapons) {
			w.restoreAmmo();
		}
	}
	/**
	 * Sets invincible mode on
	 */
	public void setInvincible() {
		if (invincible) {
			invincibleTime = System.currentTimeMillis();
			return;
		}
		invincible = true;
		invincibleThread = new Thread() {
			@Override
			public void start() {
				invincibleTime = System.currentTimeMillis();
				super.start();
			}
			public void run() {
				while (invincibleTime+10000 > System.currentTimeMillis()) {
					Thread.yield();
				}
				invincible = false;
			}
		};
		invincibleThread.start();
	}
	/**
	 * Returns if player is invincible
	 * @return if player is invincible
	 */
	public boolean isInvincible() {
		return invincible;
	}
}
