package de.hshannover.pp.slapemhard.objects;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.*;

import de.hshannover.pp.slapemhard.*;
import de.hshannover.pp.slapemhard.resources.SoundPlayer;

/**
 * Special type of person. Has lives, can hold more than one weapon and may be
 * invincible.
 * @author	Patrick Defayay<br />
 * 			Andre Schmidt<br />
 * 			Steffen Schulz<br />
 * 			Luca Zimmermann
 */
public class Player extends Person {
	private static final long serialVersionUID = 3174758635500981208L;
	private ArrayList<Weapon> weapons = new ArrayList<Weapon>();
	private Game game;
	private int activeWeapon;
	private int lives = 3;
	private boolean invincible;
	private long invincibleTime;
	private Thread invincibleThread;
	private ArrayList<Integer> holds = new ArrayList<Integer>();
	private boolean moveLeft,moveRight,jump,fire;
	private int shots, lastFired, jumped;
	
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
	protected boolean[] move(int x, int y, ArrayList<Rectangle> collisions) {
		boolean collision[] = super.move(x, y, collisions);
		try {
			for (int i = 0; i < game.getPowerUps().size(); i++) {
				if (game.getPowerUps().get(i).intersects(this)) {
					game.getPowerUps().get(i).collect();
					game.getPowerUps().remove(i);
					i--;
				}
			}
			if (collides(game.getMaliciousObjects(), 0, 0)[0]) { //any axis would be sufficient
				reduceHealth(getHealth()); //Die if not invincible
			}
			if (this.intersects(game.getTargetArea()))
				synchronized (game) {
					game.getLevel().setCompleted();
					game.notify();
				}
		} catch (NullPointerException e) {}
		return collision;
	}
	
	@Override
	public void move() {
		if (moveRight | moveLeft) {
			setWalking(true);
			if (moveLeft) {
				move(-1, 0, game.getCollisionObjects());
			} else {
				move(1, 0, game.getCollisionObjects());
			}
		} else {
			setWalking(false);
		}
		boolean[] collision = move(0, jump?-1:1, game.getCollisionObjects()); //Gravity
		if (jump | !collision[1]) {
			setJumping(true);
		} else {
			setJumping(false);
		}
		if (fire && (shots == 0 | getWeapon().getType().isAutomatic())) {
			if (lastFired == 0) {
				if (getWeapon().getAmmo()==0) {
					(new SoundPlayer("sounds/empty_shot.wav")).play();
					fire = false;
				} else {
					fire();
					shots++;
				}
			}
			lastFired = (lastFired+1) % 10;
		}
		if (jump && (collision[1] | jumped >= 70)) { //Collision with object above or reached max jump height
			jump = false;
			jumped = 0;
		} else if (jump) {//Jumped less than max jump height
			jumped++;
		}
	}
	public void setLeft(boolean b) {moveLeft=b;}
	public void setRight(boolean b) {moveRight=b;}
	public void setJump(boolean b) {jump=b; jumped = 0;}
	public void setFire(boolean b) {fire = b; shots = 0; lastFired = 0;}
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
		holds.add(type.getId());
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
	public boolean hasWeapon(int weaponId) {
		return holds.contains(weaponId);
	}
}
