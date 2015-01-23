package de.hshannover.pp.slapemhard;

import java.awt.Graphics;

import de.hshannover.pp.slapemhard.objects.Person;

public class PersonDesigner {
	private Menu menu;
	private Person p = new Person();
	
	public PersonDesigner(Menu menu) {
		this.menu = menu;
		//menu.notify();
	}
	
	public void render(Graphics g) {
		p.render(g);
	}
}
