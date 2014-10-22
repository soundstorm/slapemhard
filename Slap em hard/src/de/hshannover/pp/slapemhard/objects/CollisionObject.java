package de.hshannover.pp.slapemhard.objects;

import java.awt.Rectangle;
import java.util.ArrayList;

public class CollisionObject {
	private Rectangle size;
	public CollisionObject (Rectangle size) {
		this.size = size;
		System.out.println(size.y);
	}
	public Rectangle getPosition () {
		return size;
	}
	public boolean[] collides(ArrayList<CollisionObject> collisions, int x, int y) {
		boolean collision[] = {false,false};
		if (size.x+x <= 0)		//Out of Panel width
			collision[0] = true;
		for (CollisionObject collide : collisions) {
			final Rectangle collision_size = collide.getPosition();

			if (!((collision_size.x > size.x+size.width+x) |					//End of CollisionObject before end of Person
				(collision_size.x+collision_size.width < size.x+x) |			//Begin of CollisionObject behind end of Person
				(collision_size.y > size.y+size.height+y) |						//End of CollisionObject over end of Person
				(collision_size.y+collision_size.height < size.y+y))) {			//Begin of CollisionObject under end of Person
																				//Collision Detected. Now determine in which Direction:
				if (!((collision_size.y > size.y+size.height+y) |				//Upper bound
					(collision_size.y+collision_size.height < size.y+y)) &&		//or lower bound not outside Object height after movement
					!((collision_size.x > size.x+size.width) |					//and right bound
					(collision_size.x+collision_size.width < size.x))) {		//or left bound actually not outside Object width
					collision[1] = true;										//Collides on Y-Axis
					if (collision[0]) {											//Collides on both axis
						return collision;										//Stop further tests/iterations
					}
				}
				if (!((collision_size.x > size.x+size.width+x) |				//Right bound
						(collision_size.x+collision_size.width < size.x+x)) &&	//or left bound not outside Object width after movement
						!((collision_size.y > size.y+size.height) |				//and upper bound
						(collision_size.y+collision_size.height < size.y))) {	//or lower bound actually not outside Object height
					collision[0] = true;										//Collides on X-Axis
					if (collision[1]) {											//Collides on both axis
						return collision;										//Stop further tests/iterations
					}
				}
			}
		}
		return collision;
	}
	public void setPos(int x, int y) {
		size.x = x;
		size.y = y;
	}
}
