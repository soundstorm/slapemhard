package de.hshannover.pp.slapemhard.objects;

import java.awt.Dimension;

import de.hshannover.pp.slapemhard.SlapEmHard;

public class Weapon {
	private int ammo;
	private SlapEmHard game;
	private int angle;
	private BulletType type;
	private boolean fromPlayer;
	private boolean heading;
	
	public Weapon(SlapEmHard game, BulletType type, boolean fromPlayer) {
		this.game = game;
		this.fromPlayer = fromPlayer;
		this.type = type;
		ammo = 100;
		angle = 0;
	}
	public int getAmmo() {
		return ammo;
	}
	public void setHeading(boolean b) {
		heading = b;
	}
	public boolean getHeading() {
		return heading;
	}
	public void setAngle(int angle) {
		this.angle = angle;
	}
	public int getAngle() {
		return angle;
	}
	public void fire(Dimension origin) {
		if (ammo > 0 || !fromPlayer) {
			//Für Schrotflinte und LSD aktuell nicht geeignet.
			//play sound
			game.getBullets().add(new Bullet(game, origin, type, heading?(360-angle+180)%360:angle, fromPlayer));
			ammo--;
		} else {
			//play click sound
		}
	}
}
