package de.hshannover.pp.slapemhard.objects;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import de.hshannover.pp.slapemhard.SlapEmHard;

public class Bullet extends CollisionObject {
	private double originAngle;
	private double angle;
	private BulletType type;
	private Dimension origin;
	private int travelled;
	private ArrayList<Person> persons;
	private SlapEmHard game;
	private boolean explode,exploded;
	private long firstMove;
	private boolean heading;
	private int animationFrame;
	public Bullet(SlapEmHard game, Dimension origin, BulletType type, int degree) {
		this(game, origin, type, degree, false);
	}
	public Bullet(SlapEmHard game,Dimension origin, BulletType type, int degree, boolean fromPlayer) {
		super(new Rectangle(origin.width,origin.height,type.getSize().width,type.getSize().height));
		this.game = game;
		this.origin = origin;
		this.type = type;
		this.originAngle = Math.toRadians(degree);
		angle = this.originAngle;
		if (fromPlayer) {
			this.persons = game.getEnemies();
		} else {
			this.persons = new ArrayList<Person>();
			this.persons.add(game.getPlayer());
		}
		heading = 90<degree && degree<270;
		firstMove = System.currentTimeMillis();
	}
	/**
	 * Moves the bullet in microsteps in direction of flight and checks if it collides with any obstacle or hostile
	 * If it's a heavy object, it uses the physics of a tilted throw (gravity):
	 * x = v0*cos(α)*t
	 * y = y0<em>−g/2*t^2</em>+v0*sin(α)*t
	 * But as the y-axis is inverted (inverse coordinates), everything except y0 has to be inverted
	 * Hence these differences are there to calculate the relative movement:
	 * ∆x = x0+v0*cos(α)*t-x(t-1)
	 * ∆y = y(t-1)-y0<em>+g/2*t^2</em>-v0*sin(α)*t
	 * Otherwise the component of g/2*t^2 will be ignored.
	 */
	public void move() {
		if (explode | exploded) return;
		double t = (System.currentTimeMillis()-firstMove)/200.0;
		int x = (int)(origin.width + type.getSpeed() * Math.cos(originAngle) * t)-super.getPosition().x;
		int y = super.getPosition().y-(int)(origin.height - type.getSpeed() * Math.sin(originAngle) * t);
		if (type.getGravity()) {
			y -=  (int)4.9 * t * t;
			//degree = originAngle
		}
		//only check if bullet has moved
		if (x != 0 | y != 0) {
			boolean collision[] = super.collides(game.getCollisionObjects(), x, y);
			if (!collision[0] && !collision[1]) {
				super.setPosition(super.getPosition().x + x,
								  super.getPosition().y - y);
			} else {
				explode();
				return;
			}
			for (Person person : persons) {
				final Rectangle collision_size = person.getPosition();
	
				if (!((collision_size.x > super.getPosition().x+type.getSize().width) |			//Neither end of Bullet before end of Person
					(collision_size.x+collision_size.width < super.getPosition().x) |			//nor begin of Bullet behind end of Person
					(collision_size.y > super.getPosition().y+super.getPosition().height) |		//nor end of Bullet over end of Person
					(collision_size.y+collision_size.height < super.getPosition().y))) {		//nor begin of Bullet under end of Person
						person.reduceHealth(type.getDestruction());
						explode();
						return;
				}
			}
		}
	}
	@Override
	public void render(Graphics g) {
		
		if (explode) {
			if (animationFrame >= type.getAnimationLength()) {
				exploded = true;
				return;
			}
			g.drawImage(type.getExplosion().getTile(animationFrame), super.getPosition().x-(type.getExplosion().getWidth()-super.getPosition().width)/2, super.getPosition().y-(type.getExplosion().getHeight()-super.getPosition().height)/2, null);
			animationFrame++;
			return;
		}
		g.drawImage(getImage(), super.getPosition().x+(heading?super.getPosition().width:0), super.getPosition().y, super.getPosition().width*(heading?-1:1), super.getPosition().height, null);
	}
	@Deprecated
	public BufferedImage getImage() {
		return type.getImage();
	}
	public Dimension getSize() {
		return type.getSize();
	}
	private void explode() {
		explode = true;
		//TODO animation stuff & destruction in ambit
	}
	public boolean isExploded() {
		return exploded;
	}
	public double getAngle() {
		return angle;
	}
	public boolean getGravity() {
		return type.getGravity();
	}
}
