package de.hshannover.pp.slapemhard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SlapEmHard {
	protected static JFrame frame;
	public static void main(String[] args) {
		frame = new JFrame("Slap Em Hard");
		frame.setSize(800, 600);
		frame.setVisible(true);
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				System.out.println("ALT+F4/CMD+Q Fired");
			}
		});
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				confirmQuit();
			}
		});
	}
	
	private static void confirmQuit() {
		Object[] options = { "Beenden", "Abbrechen" };
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
		}
	}
	
	private void bla(){
		
	}
}
