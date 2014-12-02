package de.hshannover.pp.slapemhard.objects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;

import de.hshannover.pp.slapemhard.Game;
import de.hshannover.pp.slapemhard.SlapEmHard;

/**
 * General object, which can collide.
 * @author	Patrick Defayay<br />
 * 			Andre Schmidt<br />
 * 			Steffen Schulz<br />
 * 			Luca Zimmermann
 */
public class CollisionObject extends Rectangle {
	/**
	 * 
	 */
	protected static final long serialVersionUID = 1L;
	protected Game game;
	/**
	 * Creates a CollisionObject at given Position with given this.
	 * @param game
	 * @param size
	 */
	public CollisionObject (Game game, Rectangle size) {
		super(size);
		this.game = game;
	}
	public CollisionObject (Game game, int x, int y, int width, int height) {
		super(x,y,width,height);
		this.game = game;
	}
	public CollisionObject (Rectangle size) {
		super(size);
	}
	public CollisionObject(int x, int y, int width, int height) {
		super(x,y,width,height);
	}
	/**
	 * Checks if and on which axis the Object collides with any other of the List given
	 * @param collisions List of other CollisionObjects to check against
	 * @param x Check at different (relative) x-position
	 * @param y Check at different (relative) y-position
	 * @return
	 */
	public boolean[] collides(ArrayList<Rectangle> collisions, int x, int y) {
		boolean collision[] = {false,false};

		Rectangle xColl = new Rectangle(this.x+x,this.y,this.width,this.height);
		Rectangle yColl = new Rectangle(this.x,this.y+y,this.width,this.height);
		
		for (Rectangle collide : collisions) {

			//Collision Detected. Now determine in which Direction:
			if (collide.intersects(xColl)) {
				collision[0] = true;
				if (collision[1]) {				//Collides on both axis
					return collision;			//Stop further tests/iterations
				}
			}
			if (collide.intersects(yColl)) {
				collision[1] = true;
				if (collision[0]) {				//Collides on both axis
					return collision;			//Stop further tests/iterations
				}
			}
		}
		return collision;
	}
	public boolean collides(ArrayList<Rectangle> collisions) {
		for (Rectangle collide : collisions) {
			if (this.intersects(collide))
				return true;
		}
		return false;
	}
	/**
	 * Returns if the object is outside of the window, starting on the left, going clockwise.
	 * @return if the object is outside of the window, starting on the left, going clockwise.
	 */
	public boolean[] outOfWindow() {
		return outOfWindow(0,0);
		//return new boolean[] {this.x+this.width < 0, this.y+this.height < 0, this.x > game.getBounds().width, this.y > game.getBounds().height};
	}
	public boolean[] outOfWindow(int x, int y) {
		return new boolean[] {this.x+this.width+x < 0, this.y+this.height+y < 0, this.x+x > game.getWidth(), this.y+y > SlapEmHard.HEIGHT};
	}
	public boolean outOfWindow(boolean ignoreTop) {
		return this.x+width < 0 | (!ignoreTop && this.y+this.height < 0) | this.x > game.getWidth() | this.y > SlapEmHard.HEIGHT;
	}
	/**
	 * Returns if the object is touching the bounds of the window, starting on the left, going clockwise.
	 * @return if the object is touching the bounds of the window, starting on the left, going clockwise.
	 */
	public boolean[] collidesWithBounds() {
		return new boolean[] {this.x <= 0, this.y <= 0, this.x+this.width >= game.getWidth(), this.y+this.width >= SlapEmHard.HEIGHT};
	}
	/**
	 * Places the object to different (absolute) position.
	 * @param x x-Coordinate
	 * @param y y-Coordinate
	 */
	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}
	/**
	 * Legacy method. Should be implemented by any other objects extending this.
	 * @param g {@link Graphics} Object to render to
	 */
	public void render(Graphics g) {
		//TODO remove fillRect as it's just for debugging
		g.setColor(new Color(255, 0, 0, 100));
		g.fillRect(this.x, this.y, this.width, this.height);
	}
}
