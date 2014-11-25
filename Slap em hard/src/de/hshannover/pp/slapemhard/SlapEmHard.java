package de.hshannover.pp.slapemhard;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import de.hshannover.pp.slapemhard.listener.KeyboardListener;
import de.hshannover.pp.slapemhard.threads.DrawThread;
/**
 * Main program to start needed instances and Menu
 * @author SoundStorm
 *
 */
public class SlapEmHard {
	private final static JFrame frame = new JFrame("Slap Em Hard");
	public static final int WIDTH = 320;
	public static final int HEIGHT = 240;
	private static boolean fullscreen;
	private static double scale;
	private static DrawThread drawThread;
	public static void main(String[] args) {
		JFrame.setDefaultLookAndFeelDecorated(true);
		frame.setUndecorated(true);
		frame.setFocusable(true);
		frame.setResizable(false);
		
		URL iconURL = SlapEmHard.class.getResource("/res/logo.png");
		ImageIcon icon = new ImageIcon(iconURL);
		frame.setIconImage(icon.getImage());

		Object[] options = { "Ja", "Nein" };
		int n = JOptionPane.showOptionDialog(null,
				"Switch to fullscreen?", "Fullscreen",
				JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE,
				icon, options, options[0]);
		if (n == JOptionPane.YES_OPTION) {
			fullscreen = true;
		}
		
		scale = 2.0;
		frame.setSize((int)(scale*WIDTH), (int)(scale*HEIGHT));
		// http://stackoverflow.com/questions/11225113/change-screen-resolution-in-java
		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();
		GraphicsDevice device = gs[0];
		DisplayMode oldDisplayMode = device.getDisplayMode();
		frame.setVisible(true);
		Menu menu = new Menu(frame, scale);
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
					device.setDisplayMode(new DisplayMode((int)(SlapEmHard.WIDTH*scales[i]), (int)(SlapEmHard.HEIGHT*scales[i]), 32, 0));
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
				System.out.println("Quit");
			}
		});
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				confirmQuit();
			}
		});
		KeyboardListener keyboardListener = new KeyboardListener(menu);
		
		drawThread = new DrawThread(menu);
		frame.add(drawThread);
		drawThread.setPreferredSize(new Dimension(frame.getWidth(), frame.getHeight()));
		drawThread.setBounds(new Rectangle(0,0,frame.getWidth(), frame.getHeight()));
		drawThread.addKeyListener(keyboardListener);
		drawThread.setFocusable(true);
		drawThread.start();
	}
	
	/**
	 * Shows dialog if trying to close frame by {@link WindowAdapter#windowClosing(WindowEvent)}
	 */
	private static void confirmQuit() {
		System.exit(0);
		Object[] options = {"Quit", "Cancel"};
		int n = JOptionPane.showOptionDialog(null,
				"Do you really want to quit?", "Quit game",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE,
				null, options, options[1]);
		if (n == JOptionPane.OK_OPTION) {
			//drawThread.interrupt();
			System.exit(0);
		} else {
			return;
		}
	}
}
