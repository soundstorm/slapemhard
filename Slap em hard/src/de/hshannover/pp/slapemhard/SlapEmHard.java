package de.hshannover.pp.slapemhard;

import javax.imageio.ImageIO;
import javax.swing.*;

import de.hshannover.pp.slapemhard.listener.*;
import de.hshannover.pp.slapemhard.objects.*;
import de.hshannover.pp.slapemhard.threads.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.util.ArrayList;

public class SlapEmHard {
	protected static JFrame frame;
	private static Graphics graphics;
	private static ArrayList<CollisionObject> collisionObjects = new ArrayList<CollisionObject>();
	private static ArrayList<Person> enemies = new ArrayList<Person>();
	private static ArrayList<Bullet> bullets = new ArrayList<Bullet>();
	private static Player me;
	private static MoveThread mover;
	
	public SlapEmHard() {
		JFrame.setDefaultLookAndFeelDecorated(true);
		frame = new JFrame("Slap Em Hard");
		frame.setUndecorated(true);
		frame.setSize(640,480);
		//http://stackoverflow.com/questions/11225113/change-screen-resolution-in-java
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();
		GraphicsDevice device = gs[0];
		DisplayMode oldDisplayMode = device.getDisplayMode();
		frame.setVisible(true);
		frame.setResizable(false);
		if (device.isDisplayChangeSupported()) {
			try {
				throw new Exception("");
				//device.setDisplayMode(new DisplayMode(frame.getWidth(), frame.getHeight(), 32, 0));
				//device.setFullScreenWindow(frame);
			} catch (Exception e) {
				device.setFullScreenWindow(null);
				device.setDisplayMode(oldDisplayMode);
			}
		}
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		graphics = frame.getGraphics();
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				System.out.println("EXIT/ALT+F4/CMD+Q Fired");
			}
		});
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				confirmQuit();
			}
		});
		collisionObjects.add(new CollisionObject(new Rectangle(190,0,10,90)));
		collisionObjects.add(new CollisionObject(new Rectangle(100,0,10,10)));
		collisionObjects.add(new CollisionObject(new Rectangle(150,50,40,40)));
		collisionObjects.add(new CollisionObject(new Rectangle(130,70,20,20)));
		collisionObjects.add(new CollisionObject(new Rectangle(0,450,700,10)));
		//bullets.add(new Bullet(this, new Dimension(2,30), new BulletType(BulletType.BulletName.ROCKET), 20, true));
		newGame(0);
	}
	private static void confirmQuit() {
		Object[] options = {"Beenden", "Abbrechen"};
		int n = JOptionPane
				.showOptionDialog(null,
						"Möchtest du das Spiel wirklich beenden?",
						"Spiel beenden", JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.WARNING_MESSAGE,
						null,
						options,
						options[1]);
		if (n == JOptionPane.OK_OPTION) {
			System.exit(0);
		} else {
			return;
		}
	}
	public void newGame(int level) {
		me = new Player(this, 100);
		me.setWeapon(new BulletType(BulletType.BulletName.ROCKET));
		mover = new MoveThread(this);
		mover.start();
		frame.addKeyListener(new KeyboardListener(this));
		DrawGameThread drawGameThread = new DrawGameThread(this);
		drawGameThread.start();
	}
	
	public ArrayList<Person> getEnemies() {
		return enemies;
	}
	public ArrayList<Bullet> getBullets() {
		return bullets;
	}
	public ArrayList<CollisionObject> getCollisionObjects() {
		return collisionObjects;
	}
	public Player getPlayer() {
		return me;
	}
	public MoveThread getMoveThread() {
		return mover;
	}
	public Graphics getGraphics() {
		return graphics;
	}
	public JFrame getFrame() {
		return frame;
	}
}
