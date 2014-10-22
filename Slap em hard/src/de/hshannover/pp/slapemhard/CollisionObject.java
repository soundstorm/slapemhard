package de.hshannover.pp.slapemhard;

import java.awt.Rectangle;
import java.util.ArrayList;

public class CollisionObject {
	private Rectangle size;
	public CollisionObject (Rectangle size) {
		this.size = size;
	}
	public Rectangle getPosition () {
		return size;
	}
	public boolean collides(ArrayList<CollisionObject> collisions, int x, int y) {
		for (CollisionObject collide : collisions) {
			final Rectangle collision_size = collide.getPosition();
			if (((collision_size.x > size.x+size.width+x)?1:0)+					//Begin of CollisionObject behind end of Person
					((collision_size.y > size.y+size.height+y)?1:0)+
					((collision_size.x+collision_size.width+x < size.x)?1:0)+	//End of CollisionObject before end of Person
					((collision_size.y+collision_size.height+y < size.y)?1:0)
				< 2) {
				return true;
			}
		}
		return false;
	}
	public void setPos(int x, int y) {
		size.x = x;
		size.y = y;
	}
}
