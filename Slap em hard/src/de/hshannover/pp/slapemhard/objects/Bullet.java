package de.hshannover.pp.slapemhard.objects;

import java.awt.Dimension;
import java.awt.Rectangle;

public class Bullet extends CollisionObject {
	int direction;
	BulletType type;
	Dimension coord;
	Dimension origin;
	public Bullet(Dimension origin, BulletType type, int direction) {
		super(new Rectangle(origin.width,origin.height,type.getSize().width,type.getSize().height));
		this.origin = origin;
		this.type = type;
		this.direction = direction;
	}
	public void move() {
		
	}
}
