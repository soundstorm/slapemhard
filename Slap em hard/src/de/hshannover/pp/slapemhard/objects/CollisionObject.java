package de.hshannover.pp.slapemhard.objects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;

import de.hshannover.pp.slapemhard.Game;

public class CollisionObject {
	private Rectangle size;
	private Game game;
	public CollisionObject (Game game, Rectangle size) {
		this.game = game;
		this.size = size;
	}
	public Rectangle getPosition () {
		return size;
	}
	public boolean[] collides(ArrayList<CollisionObject> collisions, int x, int y) {
		boolean collision[] = {false,false};

		Rectangle xColl = new Rectangle(this.getPosition().x+x,this.getPosition().y,this.getPosition().width,this.getPosition().height);
		Rectangle yColl = new Rectangle(this.getPosition().x,this.getPosition().y+y,this.getPosition().width,this.getPosition().height);
		
		for (CollisionObject collide : collisions) {
			final Rectangle collision_size = collide.getPosition();

			//Collision Detected. Now determine in which Direction:
			if (collision_size.intersects(xColl)) {
				collision[0] = true;
				if (collision[1]) {				//Collides on both axis
					return collision;			//Stop further tests/iterations
				}
			}
			if (collision_size.intersects(yColl)) {
				collision[1] = true;
				if (collision[0]) {				//Collides on both axis
					return collision;			//Stop further tests/iterations
				}
			}
		}
		return collision;
	}
	public boolean[] outOfWindow() {
		return new boolean[] {size.x+size.width < 0, size.y+size.height < 0, size.x > game.getBounds().width, size.y > game.getBounds().height};
	}
	public boolean[] collidesWithBounds() {
		return new boolean[] {size.x <= 0, size.y <= 0, size.x+size.width >= game.getBounds().width, size.y+size.width >= game.getBounds().height};
	}
	
	@Deprecated
	public void setPos(int x, int y) {
		size.x = x;
		size.y = y;
	}
	public void setPosition(int x, int y) {
		this.size.x = x;
		this.size.y = y;
	}
	
	public void render(Graphics g) {
		//TODO remove fillRect as it's just for debugging
		g.setColor(new Color(255, 0, 0, 50));
		g.fillRect(size.x, size.y, size.width, size.height);
	}
}
