package de.hshannover.pp.slapemhard.objects;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;

import de.hshannover.pp.slapemhard.Game;

public class Bullet extends CollisionObject {
	private double originAngle;
	private int angle;
	private BulletType type;
	private Dimension origin;
	private ArrayList<Person> persons;
	private Game game;
	private boolean explode,exploded;
	private long firstMove;
	private boolean heading;
	private int animationFrame;
	private static final double g = 9.81;
	private boolean fromPlayer;
	private double cosAngle;
	private double sinAngle;
	private double tanAngle;
	public Bullet(Game game, Dimension origin, BulletType type, int degree) {
		this(game, origin, type, degree, false);
	}
	public Bullet(Game game,Dimension origin, BulletType type, int degree, boolean fromPlayer) {
		super(game, new Rectangle(origin.width,origin.height,type.getSize().width,type.getSize().height));
		this.game = game;
		this.origin = origin;
		this.type = type;
		this.originAngle = Math.toRadians(degree);
		cosAngle = Math.cos(originAngle);
		sinAngle = Math.sin(originAngle);
		tanAngle = Math.tan(originAngle);
		if (fromPlayer) {
			this.persons = game.getEnemies();
		} else {
			this.persons = new ArrayList<Person>();
			this.persons.add(game.getPlayer());
		}
		heading = 90<degree && degree<270;
		double an = this.originAngle;
		if (heading) {
			an = (Math.PI+an)%(2*Math.PI);
		}
		if (an < Math.PI/8.0 | an > Math.PI*15/8.0) {// | (angle > Math.PI*7/8.0 && angle < Math.PI*9/8.0)) {
			angle = 0; //straight
		} else if (an <= Math.PI*7/8.0) {
			angle = heading?-1:1; //upwards
		} else {
			angle = heading?1:-1; //downwards
		}
		this.fromPlayer = fromPlayer;
		firstMove = System.currentTimeMillis();
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
		double t = (System.currentTimeMillis()-firstMove)/100.0;
		int x = (int)(origin.width + type.getSpeed() * cosAngle * t)-super.getPosition().x;
		int y = super.getPosition().y-(int)(origin.height - type.getSpeed() * sinAngle * t);
		if (type.getGravity()) {
			y -=  (int)((g/2.0) * t * t);
			//angle = (2*Math.PI-Math.atan(Math.abs(super.getPosition().x-origin.width)*g*2/cosAngle/type.getSpeed()/type.getSpeed()-tanAngle))%(2*Math.PI);
			double m = Math.abs(super.getPosition().x-origin.width)*g*2/cosAngle/type.getSpeed()/type.getSpeed()-tanAngle;
			if (Math.abs(m) <= 0.4142) {
				angle = 0;
			} else {
				angle = (heading^m < 0.4142)?1:-1;
			}
		}
		
		//only check if bullet has moved
		if (x != 0 | y != 0) {
			boolean collision[] = super.collides(game.getCollisionObjects(), x, -y);
			if (collision[0] | collision[1]) {
				explode();
				return;
			}
			super.setPosition(super.getPosition().x + x,
							  super.getPosition().y - y);
			Rectangle pos = super.getPosition();
			for (Person person : persons) {
				if (pos.intersects(person.getPosition())) {
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
		} else {
			g.drawImage(type.getBulletImage().getTile(heading?4-getRelativeAngle():1+getRelativeAngle()), super.getPosition().x, super.getPosition().y, null);
		}
	}
	private int getRelativeAngle() {
		return angle;
		/*if (angle < Math.PI/8.0 | angle > Math.PI*15/8.0) {// | (angle > Math.PI*7/8.0 && angle < Math.PI*9/8.0)) {
			return 0; //straight
		} else if (angle <= Math.PI*7/8.0) {
			return heading?-1:1; //upwards
		} else {
			return heading?1:-1; //downwards
		}*/
	}
	public Dimension getSize() {
		return type.getSize();
	}
	private void explode() {
		explode = true;
		Rectangle explosion = new Rectangle(super.getPosition().x-(type.getExplosion().getWidth()-super.getPosition().width)/2, super.getPosition().y-(type.getExplosion().getHeight()-super.getPosition().height)/2, type.getExplosion().getWidth(), type.getExplosion().getHeight());
		if (fromPlayer)
		for (int i = 0; i < game.getEnemies().size(); i++) {
			if (game.getEnemies().get(i).getPosition().intersects(explosion)) {
				game.getEnemies().get(i).reduceHealth(type.getDestruction());
			}
		}
		if (game.getPlayer().getPosition().intersects(explosion)) {
			game.getPlayer().reduceHealth(type.getDestruction());
		}
		
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
