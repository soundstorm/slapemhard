package de.hshannover.pp.slapemhard;

import javax.swing.*;

import de.hshannover.pp.slapemhard.listener.*;
import de.hshannover.pp.slapemhard.objects.*;
import de.hshannover.pp.slapemhard.resources.Resource;
import de.hshannover.pp.slapemhard.threads.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;

public class Menu {
	private static JFrame frame;
	private double scale;
	private Dimension gameSize;
	private int credits;

	public Menu(JFrame frame, Dimension gameSize, double scale) {
		this.frame = frame;
		this.gameSize = gameSize;
		this.scale = scale;
		final DrawMenuThread drawMenu = new DrawMenuThread(scale, gameSize);
		frame.add(drawMenu);
		drawMenu.setPreferredSize(new Dimension(frame.getWidth(), frame.getHeight()));
		drawMenu.setBounds(new Rectangle(0,0,frame.getWidth(), frame.getHeight()));
		drawMenu.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
			}
			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyCode()) {
					case 53:
						credits++;
						System.out.println(credits);
						break;
					case 10:
						//drawMenu.removeKeyListener(this);
						drawMenu.pause(true);
						newGame();
						drawMenu.setVisible(false);
						break;
					default:
						System.out.println("KeyCode: "+e.getKeyCode());
				}
			}
			@Override
			public void keyReleased(KeyEvent e) {
			}
		});
		drawMenu.start();
		//
	}

	public void newGame() {
		(new Game(frame,gameSize,scale)).start();
	}
}
