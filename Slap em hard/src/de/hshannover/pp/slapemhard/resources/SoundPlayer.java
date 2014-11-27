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
