package de.hshannover.pp.slapemhard.objects;

import java.awt.Rectangle;

public class ObjectPrototype extends Rectangle {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5409040342889408530L;
	private int look;
	private int power;
	private int health;
	private int weapon;
	private int type;

	public ObjectPrototype(int x, int y, int width, int height) {
		super(x,y,width,height);
	}

	/**
	 * @return the look
	 */
	public int getLook() {
		return look;
	}

	/**
	 * @param look the look to set
	 */
	public void setLook(int look) {
		this.look = look;
	}

	/**
	 * @return the power
	 */
	public int getPower() {
		return power;
	}

	/**
	 * @param power the power to set
	 */
	public void setPower(int power) {
		this.power = power;
	}

	/**
	 * @return the health
	 */
	public int getHealth() {
		return health;
	}

	/**
	 * @param health the health to set
	 */
	public void setHealth(int health) {
		this.health = health;
	}

	/**
	 * @return the weapon
	 */
	public int getWeapon() {
		return weapon;
	}

	/**
	 * @param weapon the weapon to set
	 */
	public void setWeapon(int weapon) {
		this.weapon = weapon;
	}

	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}
}
