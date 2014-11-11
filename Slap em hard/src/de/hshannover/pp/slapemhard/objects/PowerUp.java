package de.hshannover.pp.slapemhard.objects;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

import de.hshannover.pp.slapemhard.Game;
import de.hshannover.pp.slapemhard.images.BufferedImageLoader;
import de.hshannover.pp.slapemhard.images.SpriteSheet;
/**
 * A power up can be collected to let the {@link Player player} get different advantages. 
 * @author SoundStorm
 *
 */
public class PowerUp extends CollisionObject {
	private static SpriteSheet ss = new SpriteSheet((new BufferedImageLoader()).getImage("images/powerups.png"),12,12);
	private int type;
	private int animationFrame;
	private boolean collected;
	private Game game;
	/**
	 * Generates a power up at a specific position and of specific type
	 * @param game
	 * @param position
	 * @param type Different types of power up:<br />
	 * 0 = MediKit<br />
	 * 1 = Coin<br />
	 * 2 = Magazine<br />
	 * 3 = Extra Life<br />
	 * 4 = Invincible
	 */
	public PowerUp(Game game, Dimension position, int type) {
		super(game,new Rectangle(position.width,position.height,12,12));
		this.game = game;
		this.type = type;
	}
	/**
	 * Renders the power up to the {@link java.awt.Graphics Graphics} object.
	 */
	public void render(Graphics g) {
		g.drawImage(ss.getTile(type, animationFrame/2), super.getPosition().x, super.getPosition().y, null);
		animationFrame = (animationFrame+1)%(ss.getCols()*2);
	}
	/**
	 * Sets variables of {@link Player Player} or {@link Game Game} according to the collected item.
	 */
	public void collect() {
		//Prevent item from being collected twice or more.
		if (collected) return;
		switch (type) {
			case 0:
				game.getPlayer().restoreHealth();
				break;
			case 1:
				game.addCoins(50);
				break;
			case 2:
				game.getPlayer().restoreAmmo();
				break;
			case 3:
				game.getPlayer().setLives(game.getPlayer().getLives()+1);
				break;
			case 4:
				game.getPlayer().setInvincible();
				break;
		}
		collected = true;
	}
	/**
	 * Returns if PowerUp is collected and can be deleted
	 * @return if PowerUp is collected and can be deleted
	 */
	public boolean collected() {
		return collected;
	}
}