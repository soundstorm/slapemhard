package de.hshannover.pp.slapemhard.objects;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;

import de.hshannover.pp.slapemhard.Game;

public class Bullet extends CollisionObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5128320310814817590L;
	private BulletType type;
	private ArrayList<Person> persons;
	private Game game;
	private boolean explode,exploded;
	private boolean heading;
	private int animationFrame;
	private boolean fromPlayer;
	
	private double cosAngle,tanAngle,sinAngle,gFactor,angleFactor,xFactor,yFactor;
	private int offsetX,offsetY;
	private double t;
	public Bullet(Game game, Dimension origin, BulletType type, int degree) {
		this(game, origin, type, degree, false);
	}
	public Bullet(Game game,Dimension origin, BulletType type, int degree, boolean fromPlayer) {
		super(game, origin.width,origin.height,type.getSize().width,type.getSize().height);
		this.game = game;
		this.type = type;
		double originAngle = Math.toRadians(degree);
		cosAngle = Math.cos(originAngle);
		sinAngle = Math.sin(originAngle);
		tanAngle = Math.tan(originAngle);
		offsetX = origin.width;
		offsetY = origin.height;
		if (fromPlayer) {
			this.persons = game.getEnemies();
		} else {
			this.persons = new ArrayList<Person>();
			this.persons.add(game.getPlayer());
		}
		heading = 90<degree && degree<270;
		
		this.fromPlayer = fromPlayer;
		double speed = type.getSpeed();//20.0;
		final double g = 9.81;
		gFactor = type.getGravity()?(int)(g/1.5):0;
		xFactor = type.getSpeed()*cosAngle;
		yFactor = type.getSpeed()*sinAngle;
		angleFactor = type.getGravity()?g*2/(cosAngle*speed*speed):0;
	}
	/**
	 * Moves the bullet in steps in direction of flight and checks if it collides with any obstacle or hostile
	 * If it's a heavy object, it uses the physics of a <strong>tilted throw (gravity)</strong>:<br />
	 * <pre>
	 * x = v0*cos(α)*t
	 * y = y0<strong>−g/2*t^2</strong>+v0*sin(α)*t
	 * </pre>
	 * But as the y-axis is inverted (inverse coordinates), everything except y0 has to be inverted.
	 * Hence these differences are there to calculate the relative movement:<br />
	 * <pre>
	 * ∆x = x0+v0*cos(α)*t-x(t-1)
	 * ∆y = y(t-1)-y0<strong>+g/2*t^2</strong>-v0*sin(α)*t
	 * </pre>
	 * Otherwise the component of g/2*t^2 will be ignored.<br />
	 * The actual angle of flight is only calculated, if the Bullet uses gravity:<br />
	 * <pre>
	 * f(x) = h0-g/(2*v0^2*cos(α))*2x^2+x*tan(α)
	 * angle = arctan(f'(x))
	 * -arctan(g^2*x*sec(α)/v0^2-tan(α))
	 * -arctan(g^2*x/cos(α)/v0^2-tan(α)
	 * </pre>
	 */
	public void move() {
		if (explode | exploded) return;
		t += 0.05;
		this.x = offsetX+(int)(xFactor*t);
		this.y = offsetY+(int)(gFactor*t*t-yFactor*t);
		//super.setPosition(offsetX+x, offsetY+y);
		if (super.collides(game.getCollisionObjects())) {
			explode();
			return;
		}
		if (super.outOfWindow(type.getGravity())) {
			exploded = true;
			return;
		}
		for (Person person : persons) {
			if (this.intersects(person)) {
				person.reduceHealth(type.getDestruction());
				explode();
				return;
			}
		}
	}
	private int getRelativeAngle() {
		double m = Math.abs(this.x)*angleFactor-tanAngle;
		return Math.abs(m) <= 0.4142?0:(heading^m < 0.4142)?1:-1;
	}
	@Override
	public void render(Graphics g) {
		
		if (explode) {
			if (animationFrame >= type.getAnimationLength()) {
				exploded = true;
				return;
			}
			g.drawImage(type.getExplosion().getTile(animationFrame), this.x-(type.getExplosion().getWidth()-this.width)/2, this.y-(type.getExplosion().getHeight()-this.height)/2, null);
			animationFrame++;
			return;
		} else {
			g.drawImage(type.getBulletImage().getTile(heading?4-getRelativeAngle():1+getRelativeAngle()), this.x, this.y, null);
		}
	}
	public Dimension getSize() {
		return type.getSize();
	}
	private void explode() {
		explode = true;
		Rectangle explosion = new Rectangle(this.x-(type.getExplosion().getWidth()-this.width)/2, this.y-(type.getExplosion().getHeight()-this.height)/2, type.getExplosion().getWidth(), type.getExplosion().getHeight());
		if (fromPlayer)
		for (int i = 0; i < game.getEnemies().size(); i++) {
			if (game.getEnemies().get(i).intersects(explosion)) {
				game.getEnemies().get(i).reduceHealth(type.getDestruction());
			}
		}
		if (game.getPlayer().intersects(explosion)) {
			game.getPlayer().reduceHealth(type.getDestruction());
		}
		
	}
	public boolean isExploded() {
		return exploded;
	}
	public boolean getGravity() {
		return type.getGravity();
	}
}
