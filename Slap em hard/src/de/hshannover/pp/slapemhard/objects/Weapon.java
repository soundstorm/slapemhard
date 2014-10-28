package de.hshannover.pp.slapemhard.objects;

import java.awt.Dimension;

import de.hshannover.pp.slapemhard.SlapEmHard;

public class Weapon {
	private int ammo;
	private SlapEmHard game;
	private int angle;
	private BulletType type;
	private boolean fromPlayer;
	
	public Weapon(SlapEmHard game, BulletType type, boolean fromPlayer) {
		this.game = game;
		this.fromPlayer = fromPlayer;
		this.type = type;
		ammo = 50;
		angle = 20;
	}
	public int getAmmo() {
		return ammo;
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
			game.getBullets().add(new Bullet(game, origin, type, angle, fromPlayer));
			ammo--;
		} else {
			//play click sound
		}
	}
}
