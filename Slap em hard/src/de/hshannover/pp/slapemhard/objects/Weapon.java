package de.hshannover.pp.slapemhard.objects;

import java.awt.Dimension;

import de.hshannover.pp.slapemhard.Game;
/**
 * A weapon can be fired.
 * @author SoundStorm
 *
 */
public class Weapon {
	private int ammo;
	private Game game;
	private int angle;
	private BulletType type;
	private boolean fromPlayer;
	private boolean heading;
	public Weapon(Game game, BulletType type) {
		this(game, type, false);
	}
	/**
	 * Creates a Weapon based on a {@link BulletType}
	 * @param game
	 * @param type
	 * @param fromPlayer
	 */
	public Weapon(Game game, BulletType type, boolean fromPlayer) {
		this.game = game;
		this.fromPlayer = fromPlayer;
		this.type = type;
		ammo = type.getAmmo();
		angle = 0;
	}
	/**
	 * Returns the {@link BulletType} of Weapon
	 * @return the {@link BulletType} of Weapon
	 */
	public BulletType getType() {
		return type;
	}
	/**
	 * Returns the remaining ammo of weapon
	 * @return the remaining ammo of weapon
	 */
	public int getAmmo() {
		return ammo;
	}
	/**
	 * Sets the heading
	 * @param heading The heading (1 or 0 for facing left or right)
	 */
	public void setHeading(boolean heading) {
		this.heading = heading;
	}
	/**
	 * Returns the heading (1 or 0 for facing left or right)
	 * @return the heading (1 or 0 for facing left or right)
	 */
	public boolean getHeading() {
		return heading;
	}
	/**
	 * Sets the relative angle.
	 * @param angle The relative angle (-1, 0, 1 for down, straight, up)
	 */
	public void setAngle(int angle) {
		this.angle = angle;
	}
	/**
	 * Returns the relative angle (-1, 0, 1 for down, straight, up)
	 * @return the relative angle (-1, 0, 1 for down, straight, up)
	 */
	public int getAngle() {
		return angle;
	}
	/**
	 * Fires a bullet and reduces ammo if fired by {@link Player Player}.
	 * @param origin Origin of the bullet
	 */
	public void fire(Dimension origin, boolean heading) {
		if (ammo > 0 || !fromPlayer) {
			//TODO play sound
			origin.width -= type.getSize().width/2; //Center
			origin.width += type.getOffsets().get(1+angle).width*(heading?-1:1);
			origin.height+= type.getOffsets().get(1+angle).height;
			game.getBullets().add(new Bullet(game, origin, type, (360+(heading?-angle*45+180:angle*45)+(int)(type.getPrecision()*Math.random()-type.getPrecision()/2))%360, fromPlayer));
			if (fromPlayer)
				ammo--;
		} else {
			//play click sound
		}
	}
	/**
	 * Restores the maximum ammo of the weapon
	 */
	public void restoreAmmo() {
		this.ammo = type.getAmmo();
	}
}
