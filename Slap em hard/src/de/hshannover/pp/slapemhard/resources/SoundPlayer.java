package de.hshannover.pp.slapemhard.resources;

import java.net.URL;
import java.util.ArrayList;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class SoundPlayer {
	private static ArrayList<String> paths = new ArrayList<String>();
	private static ArrayList<AudioInputStream> streams = new ArrayList<AudioInputStream>();
	
	public Clip play(String relPath, double loudness) {
		try {
			Clip clip = AudioSystem.getClip();
			int index = paths.indexOf(relPath);
			AudioInputStream stream;
			if (index >= 0) {
				stream = streams.get(index);
			} else {
				URL url = this.getClass().getClassLoader().getResource("/res/"+relPath);
				stream = AudioSystem
						.getAudioInputStream(url);
				streams.add(stream);
				paths.add(relPath);
			}
			FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
			gainControl.setValue((float)loudness);
			clip.open(stream);
			clip.start();
			return clip;
		} catch (Exception e) {
			//e.printStackTrace();
			return null;
		}
	}
}
