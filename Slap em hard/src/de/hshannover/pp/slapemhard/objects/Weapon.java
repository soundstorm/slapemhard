package de.hshannover.pp.slapemhard.objects;

import java.awt.Dimension;

import de.hshannover.pp.slapemhard.Game;

public class Weapon {
	private int ammo;
	private Game game;
	private int angle;
	private BulletType type;
	private boolean fromPlayer;
	private boolean heading;
	
	public Weapon(Game game, BulletType type, boolean fromPlayer) {
		this.game = game;
		this.fromPlayer = fromPlayer;
		this.type = type;
		ammo = type.getAmmo();
		angle = 0;
	}
	public BulletType getType() {
		return type;
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
			if (!heading) {
				origin.width+=16;
			} else {
				origin.width-=type.getSize().width;
			}
			origin.width+=type.getOffsets().get(1+angle).width*(heading?-1:1);
			origin.height+=type.getOffsets().get(1+angle).height;
			game.getBullets().add(new Bullet(game, origin, type, (360+(heading?-angle*45+180:angle*45))%360, fromPlayer));
			ammo--;
		} else {
			//play click sound
		}
	}
	public void restoreAmmo() {
		this.ammo = type.getAmmo();
	}
}
