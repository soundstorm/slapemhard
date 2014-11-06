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
	protected static JFrame frame;
	private boolean fullscreen;
	private double scale;
	private Dimension gameSize;
	private int credits;

	public Menu() {
		JFrame.setDefaultLookAndFeelDecorated(true);
		frame = new JFrame("Slap Em Hard");
		frame.setUndecorated(true);
		URL iconURL = getClass().getResource("/res/images/icon.png");
		ImageIcon icon = new ImageIcon(iconURL);
		frame.setIconImage(icon.getImage());

		Object[] options = { "Ja", "Nein" };
		int n = JOptionPane.showOptionDialog(null,
				"Switch to fullscreen?", "Fullscreen",
				JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE,
				null, options, options[0]);
		if (n == JOptionPane.YES_OPTION) {
			fullscreen = true;
		}
		gameSize = new Dimension(320,240);
		Resource r = new Resource();
		
		scale = 2.5;
		frame.setSize((int)(scale*gameSize.width), (int)(scale*gameSize.height));
		// http://stackoverflow.com/questions/11225113/change-screen-resolution-in-java
		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();
		GraphicsDevice device = gs[0];
		DisplayMode oldDisplayMode = device.getDisplayMode();
		frame.setVisible(true);
		frame.setResizable(false);
		/*Preferred sizes:
		SCALEFACTOR	NAME	RESOLUTION
		5			UXGA	1600x1200
		4.5					1440x1080
		4					1280x 960
		3.6			XGA+	1152x 964
		3.2			XGA		1024x 768
		2.5			SVGA	 800x 600
		2.4			PAL		 768x 576
		2			VGA		 640x 480
		1			QVGA	 320x 240
		
		*/
		double scales[] = {1,2,4,5,4.5,2.5,3.6,3.2,2.4};
		if (device.isDisplayChangeSupported() && device.isFullScreenSupported() && fullscreen) {
			for (int i = 0; i < scales.length; i++) {
				try {
					//throw new Exception("");
					//device.setDisplayMode(new DisplayMode(frame.getWidth(), frame.getHeight(), 32, 0));
					device.setDisplayMode(new DisplayMode((int)(gameSize.width*scales[i]), (int)(gameSize.height*scales[i]), 32, 0));
					device.setFullScreenWindow(frame);
					Toolkit toolkit = Toolkit.getDefaultToolkit();
					Point hotSpot = new Point(0, 0);
					BufferedImage cursorImage = new BufferedImage(1, 1,
							BufferedImage.TRANSLUCENT);
					Cursor invisibleCursor = toolkit.createCustomCursor(
							cursorImage, hotSpot, "InvisibleCursor");
					frame.setCursor(invisibleCursor);
					fullscreen = true;
					scale = scales[i];
					break;
				} catch (Exception e) {
					fullscreen = false;
				}
			}
			if (!fullscreen) {
				device.setFullScreenWindow(null);
				device.setDisplayMode(oldDisplayMode);
				JOptionPane.showMessageDialog(frame, "Fullscreen is not supported.");
			}
		}
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		//graphics = frame.getGraphics();
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
						new Game(frame,gameSize,scale);
						drawMenu.pause(true);
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
	

	private static void confirmQuit() {
		Object[] options = {"Quit", "Cancel"};
		int n = JOptionPane.showOptionDialog(null,
				"Do you really want to quit?", "Quit game",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE,
				null, options, options[1]);
		if (n == JOptionPane.OK_OPTION) {
			System.exit(0);
		} else {
			return;
		}
	}

	@Deprecated
	public void newGame(int level) {
		new Game(frame,gameSize,scale);
	}
}
