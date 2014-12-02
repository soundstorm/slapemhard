/*package de.hshannover.pp.slapemhard.resources;

import java.applet.AudioClip;
import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineEvent.Type;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SoundPlayer {
	private float loudness;
	private String fileName;
	private boolean repeat;
	private boolean playing;
	AudioClip sound;
	private URL res;
	Clip clip;
	
	public SoundPlayer(String fileName) {
		this(fileName, 0);
	}
	
	public SoundPlayer(String fileName, float loudness) {
		this.fileName = fileName;
		if (loudness < -80) {
			loudness = -80;
		}
		this.loudness = loudness;
		try {
			res = SoundPlayer.class.getResource("/res/"
					+ fileName);
			//AudioSystem.getClip();
			//sound = Applet.newAudioClip(res);
			//FloatControl fC = (FloatControl) sound.g
		} catch (Exception e) {}
	}
	
	public void setRepeat(boolean repeat) {
		this.repeat = repeat;
	}

	public void play() {
		if (playing) return;
		if (res == null) return;
		if (loudness == -80) return;
		if (fileName == null) return;
		try {
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(res);
			clip = AudioSystem.getClip();
			clip.open(audioInputStream);
		} catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
			e.printStackTrace();
		}
		FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
		gainControl.setValue(loudness);
		if (repeat)
			clip.loop(Clip.LOOP_CONTINUOUSLY);
		else
			clip.loop(0);
		clip.start();
		playing = true;
		clip.addLineListener(new LineListener() {
			@Override
			public void update(LineEvent event) {
				if (event.getType() == Type.STOP) {
					clip.close();
					System.out.println("CLOSED");
				}
			}
			
		});
	}
	@Deprecated
	public void stopAudio() {
		stop();
	}

	public void stop() {
		clip.stop();
		//sound.stop();
		playing = false;
		System.out.println("STOPPED AUDIO");
	}
	
	public void close() {
		clip.close();
	}
}*/

package de.hshannover.pp.slapemhard.resources;

import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.SourceDataLine;

public class SoundPlayer implements Runnable {
	private final int BUFFER_SIZE = 256000;//128000;
	private URL soundFile;
	private AudioInputStream audioStream;
	private AudioFormat audioFormat;
	private SourceDataLine sourceLine;
	private float loudness;
	private String fileName;
	private boolean repeat;
	private Thread thread;
	private boolean playing;
	
	public SoundPlayer(String fileName) {
		this(fileName, 0);
	}
	
	public SoundPlayer(String fileName, float loudness) {
		super();
		this.fileName = fileName;
		if (loudness < -80) {
			loudness = -80;
		}
		this.loudness = loudness;
	}
	
	public void setRepeat(boolean repeat) {
		this.repeat = repeat;
	}

	public void play() {
		if (playing) return;
		if (loudness == -80) return;
		if (fileName == null) return;
		thread = new Thread(this,"Audio Playback "+fileName);
		playing = true;
		thread.start();
	}

	public void stopAudio() {
		//thread.interrupt();
		sourceLine.stop();
		playing = false;
		System.out.println("STOPPED AUDIO");
	}
	
	@Override
	public void run() {
		try {
			soundFile = SoundPlayer.class.getResource("/res/"
					+ fileName);
		} catch (Exception e) {
			return;
		}
		do {
			try {
				audioStream = AudioSystem.getAudioInputStream(soundFile);
			} catch (Exception e) {
				return;
			}
			
			audioFormat = audioStream.getFormat();
			
			DataLine.Info info = new DataLine.Info(SourceDataLine.class,
					audioFormat);
			try {
				sourceLine = (SourceDataLine) AudioSystem.getLine(info);
				sourceLine.open(audioFormat);
			} catch (Exception e) {
				return;
			}
			try {
				FloatControl fC =  (FloatControl) sourceLine.getControl(FloatControl.Type.MASTER_GAIN);
				fC.setValue(loudness);
			} catch (IllegalArgumentException e) {}
			sourceLine.start();
			int nBytesRead = 0;
			byte[] abData = new byte[BUFFER_SIZE];
			while (nBytesRead != -1 && playing) {
				try {
					nBytesRead = audioStream.read(abData, 0, abData.length);
				} catch (IOException e) {
					//return;
				}
				if (nBytesRead >= 0) {
					@SuppressWarnings("unused")
					int nBytesWritten = sourceLine.write(abData, 0, nBytesRead);
				}
			}
			sourceLine.drain();
			sourceLine.close();
		} while (repeat && playing);
	}
}
