package de.hshannover.pp.slapemhard;

import javax.swing.*;

import de.hshannover.pp.slapemhard.objects.*;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class SlapEmHard {
	protected static JFrame frame;
	private static Graphics graphics;
	private static ArrayList<CollisionObject> collisionObjects = new ArrayList<CollisionObject>();
	private static ArrayList<Enemy> enemies = new ArrayList<Enemy>();
	private static ArrayList<Bullet> bullets = new ArrayList<Bullet>();
	private static Player me = new Player(100);
	private static MoveThread mover;
	public static void main(String[] args) {
		JFrame.setDefaultLookAndFeelDecorated(true);
		frame = new JFrame("Slap Em Hard");
		frame.setUndecorated(true);
		frame.setSize(800, 600);
		frame.setVisible(true);
		frame.setResizable(false);
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
		collisionObjects.add(new CollisionObject(new Rectangle(120,0,10,10)));
		collisionObjects.add(new CollisionObject(new Rectangle(150,50,40,40)));
		collisionObjects.add(new CollisionObject(new Rectangle(130,70,20,20)));
		collisionObjects.add(new CollisionObject(new Rectangle(0,90,200,10)));
		mover = new MoveThread(me,collisionObjects);
		mover.start();
		frame.addKeyListener(new KeyListener() {
			private boolean spacePressed;	//Only allow jumping once when pressed
			@Override
			public void keyTyped(KeyEvent e){}
			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyCode()) {
					case 32: //SPACE
						if (!spacePressed) {
							boolean collision[] = me.collides(collisionObjects,0,1);	//Check if on floor
							if (collision[1]) {											//Only Jump, when on floor
								mover.setJump(true);
							}
							spacePressed = true;
						}
						break;
					case 65: case 37: //A <
						mover.setLeft(true);
						break;
					case 68: case 39: //D >
						mover.setRight(true);
						break;
					default:
						System.out.println(e.getKeyCode());
				}
			}
			@Override
			public void keyReleased(KeyEvent e) {
				switch (e.getKeyCode()) {
				case 32: //SPACE
					mover.setJump(false);
					spacePressed = false;
					break;
				case 65: case 37: //A <
					mover.setLeft(false);
					break;
				case 68: case 39: //D >
					mover.setRight(false);
					break;
				default:
					System.out.println(e.getKeyCode());
			}
				
			}
		});
		(new Thread() {
			boolean running;
			@Override
			public void start() {
				running = true;
				super.start();
			}
			@Override
			public synchronized void run() {
				while (running) {
					int xoffset = me.getPosition().x-100;
					int yoffset = 0;
					if (xoffset < 0) {
						xoffset = 0;
					}
					graphics.setColor(Color.GRAY);
					graphics.fillRect(0, 0, frame.getWidth(), frame.getHeight());
					graphics.setColor(Color.RED);
					for (Enemy obj : enemies) {
						graphics.drawRect(obj.getPosition().x-xoffset, obj.getPosition().y, obj.getPosition().width, obj.getPosition().height);
					}
					graphics.setColor(Color.WHITE);
					for (Bullet obj : bullets) {
						graphics.drawRect(obj.getPosition().x-xoffset, obj.getPosition().y, obj.getPosition().width, obj.getPosition().height);
					}
					graphics.setColor(Color.YELLOW);
					for (CollisionObject obj : collisionObjects) {
						graphics.fillRect(obj.getPosition().x-xoffset, obj.getPosition().y, obj.getPosition().width, obj.getPosition().height);
						//System.out.println(obj.getPosition().y);
					}
					graphics.setColor(Color.GREEN);
					graphics.drawRect(me.getPosition().x-xoffset, me.getPosition().y, me.getPosition().width, me.getPosition().height);
					try {
						sleep(10);
					} catch (InterruptedException e) {
						
					}
				}
			}
		}).start();
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
	
/*	public ArrayList<Enemy> getEnemies() {
		return enemies;
	}
	public ArrayList<Bullet> getBullets() {
		return bullets;
	}
	public ArrayList<CollisionObject> getCollisionObjects() {
		return collisionObjects;
	}*/
}
