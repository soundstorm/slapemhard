package de.hshannover.pp.slapemhard.listener;

import java.awt.event.*;

import de.hshannover.pp.slapemhard.*;

/**
 * Implementation of KeyListener for Games.<br />
 * Default keyboard layout for mame emulation:
 * <table>
 * <tr><td>5		</td><td>Insert Coin						</td><td>CODE 53</td></tr>
 * <tr><td>1		</td><td>Start								</td><td>CODE 49</td></tr>
 * <tr><td>Arrows	</td><td>Movement							</td><td>37 &lt;<br />38 ^<br />39 &gt;<br />40 v</td></tr>
 * <tr><td>Ctrl		</td><td>Arcade 1   (normally A/jump/fire)	</td><td>CODE 17</td></tr>
 * <tr><td>Alt		</td><td>Arcade 2   (normally B/bomb)		</td><td>CODE 18</td></tr>
 * <tr><td>Space	</td><td>Arcade 3   (other function)		</td><td>CODE 32</td></tr>
 * <tr><td>P		</td><td>Pause								</td><td>CODE 80</td></tr>
 * <tr><td>F2		</td><td>Service Mode						</td><td>CODE 113</td></tr>
 * <tr><td>Tab		</td><td>Select Game menu - do not use.		</td><td>not detected by keyEvent</td></tr>
 * <tr><td>ESC		</td><td>Quit								</td><td>CODE 27</td></tr>
 * </table>
 * Codes for keys follow ASCII-Table.<br />
 * But ctrl+Arrow Keys interferes on OS X with "Spaces", use fullscreen to override this behavior.<br />
 * Used configuration for this game:
 * <table>
 * <tr><td>5		</td><td>Add Credit</td></tr>
 * <tr><td>1		</td><td>Start</td></tr>
 * <tr><td>Left		</td><td>Move left</td></tr>
 * <tr><td>Right	</td><td>Move right</td></tr>
 * <tr><td>Up		</td><td>Hold weapon up</td></tr>
 * <tr><td>Down		</td><td>Hold weapon down</td></tr>
 * <tr><td>Ctrl		</td><td>Jump</td></tr>
 * <tr><td>Alt		</td><td>Fire</td></tr>
 * </table>
 * @see java.awt.event.KeyListener (java.awt.event.KeyEvent)
 */
public class KeyboardListener implements KeyListener {
	private boolean spacePressed;	//Only allow jumping once when pressed
	Menu menu;
	/**
	 * 
	 * @param menu Superclass
	 */
	public KeyboardListener (Menu menu) {
		//super();
		this.menu = menu;
	}
	/**
	 * Does nothing, just implemented.
	 * @see java.awt.event.KeyListener#keyTyped (java.awt.event.KeyEvent)
	 */
	@Override
	public void keyTyped(KeyEvent e){
	}
	/**
	 * Sets the moving thread to move accordingly, fires the weapon or maintains the menu.
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_5) {
			menu.addCredits();
			return;
		}
		if (menu.getLevel() != null) {
			toggleEvent(e.getKeyCode(),true);
		} else if (menu.getGame() != null) {
			menu.getGame().keyEvent(e.getKeyCode());
		} else {
			menu.keyEvent(e.getKeyCode());
		}
	}
	/**
	 * Sets the threads to stop behavior according to the key if any of the keys is released.
	 * @see java.awt.event.KeyListener#keyReleased (java.awt.event.KeyEvent)
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		if (menu.getLevel() != null)
			toggleEvent(e.getKeyCode(), false);
	}
	
	private void toggleEvent(int keyCode, boolean state) {
		Level level = menu.getLevel();
		switch (keyCode) {
			case KeyEvent.VK_ESCAPE:
				synchronized (menu.getGame()) {
					menu.getGame().notify();
				}
			case KeyEvent.VK_CONTROL: case KeyEvent.VK_SHIFT:
				if (!spacePressed && state) {
					boolean collision[] = level.getPlayer().collides(level.getCollisionObjects(),0,1);	//Check if on floor
					if (collision[1]) {																	//Only Jump, when on floor
						level.getPlayer().setJump(true);
					}
				} else if (!state) {
					level.getPlayer().setJump(false);
				}
				spacePressed = state;
				break;
			case KeyEvent.VK_ALT: case KeyEvent.VK_Y: case KeyEvent.VK_COMMA:
				level.getPlayer().setFire(state);
				break;
			case KeyEvent.VK_L: case KeyEvent.VK_X:
				if (state)
					level.getPlayer().changeWapon();
				break;
			case KeyEvent.VK_LEFT: case KeyEvent.VK_A:
				level.getPlayer().setLeft(state);
				if (state)
					level.getPlayer().setRight(false);
				break;
			case KeyEvent.VK_RIGHT: case KeyEvent.VK_D:
				level.getPlayer().setRight(state);
				if (state)
					level.getPlayer().setLeft(false);
				break;
			case KeyEvent.VK_UP: case KeyEvent.VK_W:
				level.getPlayer().getWeapon().setAngle(state?1:0);
				break;
			case KeyEvent.VK_DOWN: case KeyEvent.VK_S:
				level.getPlayer().getWeapon().setAngle(state?-1:0);
				break;
			default:
				System.out.println("KeyCode: "+keyCode);
		}
	}
}