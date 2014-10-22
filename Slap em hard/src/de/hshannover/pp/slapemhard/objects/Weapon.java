package de.hshannover.pp.slapemhard.objects;

import java.awt.Dimension;

import de.hshannover.pp.slapemhard.SlapEmHard;

public class Weapon {
	private int ammo;
	private SlapEmHard self;
	
	public Weapon(SlapEmHard self) {
		this.self = self;
	}
	public int getAmmo() {
		return ammo;
	}
	public void fire(int angle, Dimension position) {
		this.fire(angle, position, true);
	}
	public void fire(int angle, Dimension position, boolean enemy) {
		
	}
}
