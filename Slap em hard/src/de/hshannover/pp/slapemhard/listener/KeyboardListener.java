package de.hshannover.pp.slapemhard.listener;

import java.awt.event.*;

import de.hshannover.pp.slapemhard.SlapEmHard;

/**
 * @see java.awt.event.KeyListener (java.awt.event.KeyEvent)
 * Implementation of KeyListener for Games.
 * Default keyboard layout for mame emulation:
 * 5      -> Insert Coin                       CODE 53
 * 1      -> Start                             CODE 49
 * Arrows -> Movement                          37 <, 38 ^, 39 >, 40 v
 * Ctrl   -> Arcade 1   (normally A/jump/fire) CODE 17
 * Alt    -> Arcade 2   (normally B/bomb)      CODE 18
 * Space  -> Arcade 3   (other function)       CODE 32
 * P      -> Pause                             CODE 80
 * F2     -> Service Mode                      CODE 113
 * Tab    -> Select Game menu - do not use.    not detected by keyEvent 
 * Codes for keys follow ASCII-Table.
 * But ctrl+Arrow Keys interferes on OS X with "Spaces"
 */
public class KeyboardListener implements KeyListener {
	private boolean spacePressed;	//Only allow jumping once when pressed
	SlapEmHard game;
	/**
	 * 
	 * @param game Superclass
	 */
	public KeyboardListener (SlapEmHard game) {
		//super();
		this.game = game;
	}
	/**
	 * Does nothing, just implemented.
	 * @see java.awt.event.KeyListener#keyTyped (java.awt.event.KeyEvent)
	 */
	@Override
	public void keyTyped(KeyEvent e){}
	/**
	 * Sets the moving thread to move accordingly, fires the weapon or maintains the menu.
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
			case 17: //Ctrl/Strg
				if (!spacePressed) {
					boolean collision[] = game.getPlayer().collides(game.getCollisionObjects(),0,1);	//Check if on floor
					if (collision[1]) {																	//Only Jump, when on floor
						game.getMoveThread().setJump(true);
					}
					spacePressed = true;
				}
				break;
			case 18: //Alt
				//fire
				game.getPlayer().fire();
				break;
			case 27: //ESC
				//menu
				break;
			case 65: case 37: //A <
				game.getMoveThread().setLeft(true);
				break;
			case 68: case 39: //D >
				game.getMoveThread().setRight(true);
				break;
			default:
				System.out.println(e.getKeyCode());
		}
	}
	/**
	 * Sets the threads to stop behavior according to the key if any of the keys is released.
	 * @see java.awt.event.KeyListener#keyReleased (java.awt.event.KeyEvent)
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
			case 17: //Ctrl/Strg
				game.getMoveThread().setJump(false);
				spacePressed = false;
				break;
			case 18: //Alt
				//fire
				break;
			case 27: //ESC
				//menu
				break;
			case 65: case 37: //A <
				game.getMoveThread().setLeft(false);
				break;
			case 68: case 39: //D >
				game.getMoveThread().setRight(false);
				break;
			default:
				System.out.println(e.getKeyCode());
		}
	}
}