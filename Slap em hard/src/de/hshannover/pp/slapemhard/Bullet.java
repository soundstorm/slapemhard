package de.hshannover.pp.slapemhard;

import java.awt.Dimension;

public class Bullet {
	float direction;
	BulletType type;
	Dimension coord;
	Dimension origin;
	public Bullet(Dimension origin, BulletType type, float direction) {
		this.origin = origin;
		this.type = type;
		this.direction = direction;
	}
}
