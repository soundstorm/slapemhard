package de.hshannover.pp.slapemhard.objects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;

import de.hshannover.pp.slapemhard.Game;

/**
 * General object, which can collide.
 * @author SoundStorm
 *
 */
public class CollisionObject {
	private Rectangle size;
	protected Game game;
	/**
	 * Creates a CollisionObject at given Position with given size.
	 * @param game
	 * @param size
	 */
	public CollisionObject (Game game, Rectangle size) {
		this.game = game;
		this.size = size;
	}
	/**
	 * Returns position and size
	 * @return position and size
	 */
	public Rectangle getPosition () {
		return size;
	}
	/**
	 * Checks if and on which axis the Object collides with any other of the List given
	 * @param collisions List of other CollisionObjects to check against
	 * @param x Check at different (relative) x-position
	 * @param y Check at different (relative) y-position
	 * @return
	 */
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
	/**
	 * Returns if the object is outside of the window, starting on the left, going clockwise.
	 * @return if the object is outside of the window, starting on the left, going clockwise.
	 */
	public boolean[] outOfWindow() {
		return new boolean[] {size.x+size.width < 0, size.y+size.height < 0, size.x > game.getBounds().width, size.y > game.getBounds().height};
	}
	/**
	 * Returns if the object is touching the bounds of the window, starting on the left, going clockwise.
	 * @return if the object is touching the bounds of the window, starting on the left, going clockwise.
	 */
	public boolean[] collidesWithBounds() {
		return new boolean[] {size.x <= 0, size.y <= 0, size.x+size.width >= game.getBounds().width, size.y+size.width >= game.getBounds().height};
	}
	/**
	 * Places the object to different (absolute) position.
	 * @param x x-Coordinate
	 * @param y y-Coordinate
	 */
	public void setPosition(int x, int y) {
		this.size.x = x;
		this.size.y = y;
	}
	/**
	 * Legacy method. Should be implemented by any other objects extending this.
	 * @param g {@link Graphics} Object to render to
	 */
	public void render(Graphics g) {
		//TODO remove fillRect as it's just for debugging
		g.setColor(new Color(255, 0, 0, 50));
		g.fillRect(size.x, size.y, size.width, size.height);
	}
}
