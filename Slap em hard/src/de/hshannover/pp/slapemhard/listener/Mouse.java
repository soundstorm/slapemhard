package de.hshannover.pp.slapemhard.listener;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import de.hshannover.pp.slapemhard.Menu;
/**
 * @author	Patrick Defayay<br />
 * 			Andre Schmidt<br />
 * 			Steffen Schulz<br />
 * 			Luca Zimmermann
 */
public class Mouse implements MouseWheelListener, MouseListener, MouseMotionListener {
	
	private Menu menu;

	public Mouse(Menu menu) {
		this.menu = menu;
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		menu.mouseWheel(e.getWheelRotation());
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		menu.mousePressed(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		menu.mouseReleased(e);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		System.out.println("ENTERED");
	}

	@Override
	public void mouseExited(MouseEvent e) {
		System.out.println("EXITED");
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		menu.mouseDragged(e);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		//System.out.println("MOVED");
		
	}

}
