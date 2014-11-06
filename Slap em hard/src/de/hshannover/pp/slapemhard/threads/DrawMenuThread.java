package de.hshannover.pp.slapemhard.threads;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;

import de.hshannover.pp.slapemhard.images.BufferedImageLoader;

public class DrawMenuThread extends Canvas implements Runnable {
	private static final long serialVersionUID = 1L;
	boolean running;
	private Thread thread;
	int fps = 30;
	double scale;
	//public static int WIDTH, HEIGHT;
	private Dimension gameSize;
	private int menu = 0;
	private Font font;
	private int selected;
	private boolean paused;
	
	public DrawMenuThread(double scale, Dimension gameSize) {
		super();
		this.scale = scale;
		this.gameSize = gameSize;
		setIgnoreRepaint(true);
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/res/fonts/VCR_OSD_MONO.ttf"));
		} catch (IOException|FontFormatException e) {
			System.out.println("Failed to load font");
		}
	}
	
	private void init(){
	}
	
	public synchronized void start(){
		if(running){
			return;
		}
		running = true;
		thread = new Thread(this, "Menu Rendering Thread");
		thread.start();
	}
	@Override
	public synchronized void run() {
		init();
		this.requestFocus();
		long timer = System.currentTimeMillis();
		int frames = 0;
		long waitDuration = 0L;
		while(running){
			long now = System.nanoTime();
			render();
			frames++;
					
			if(System.currentTimeMillis() - timer > 1000){
				timer += 1000;
				fps = frames;
				frames = 0;
			}
			if (fps < 20) {
				//Don't wait if frame rate drops
				waitDuration = 0;
				System.out.println("FPS DROPPED BELOW 20!");
			} else {
				waitDuration = 30000000-(System.nanoTime()-now);
				if (waitDuration < 0) {
					waitDuration = 0;
				}
			}
			try {
				Thread.sleep((int)(waitDuration/1000000), (int)(waitDuration%1000000));
			} catch (InterruptedException e) {}
		}
	}
	
	public void pause(boolean paused) {
		this.paused = paused;
	}
	
	private void render(){
		BufferStrategy bs = this.getBufferStrategy();
		if(bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		
		Graphics g = bs.getDrawGraphics();
		Graphics2D g2d = (Graphics2D) g;
		g2d.scale(scale, scale);
		////////////////////////////////////

		switch (menu) {
			//Main Menu
			case 0:
				//Background
				g.setColor(Color.RED);
				g.fillRect(0,0,gameSize.width,gameSize.height);
				//Header
				g.setColor(Color.WHITE);
				g.setFont(font.deriveFont(Font.PLAIN,28));
				g.drawString("Slap Em Hard",2,50);
				
				g.setFont(font.deriveFont(Font.PLAIN,18));
				
				g.setColor(selected==0?Color.YELLOW:Color.WHITE);
					g.fillRect((gameSize.width-200)/2, 100, 200, 20);
					g.setColor(Color.BLACK);
					g.drawString("New Game",(gameSize.width-160)/2+2,118);
				
				g.setColor(selected==1?Color.YELLOW:Color.WHITE);
					g.fillRect((gameSize.width-200)/2, 140, 200, 20);
					g.setColor(Color.BLACK);
					g.drawString("How to play",(gameSize.width-190)/2+2,158);
					
				g.setColor(selected==2?Color.YELLOW:Color.WHITE);
					g.fillRect((gameSize.width-200)/2, 180, 200, 20);
					g.setColor(Color.BLACK);
					g.drawString("Credits",(gameSize.width-120)/2+2,198);
				break;
			case 2:
				g.setFont(font.deriveFont(Font.PLAIN,20));
				String howToPlay = "Controls:\n"
						+ "Left/Right  Move left/right\n"
						+ "Up/Down     Weapon up/down\n"
						+ "Strg/Ctrl   Fire\n"
						+ "Alt         Jump\n"
						+ "Space       Change Weapon\n"
						+ "P           Pause\n"
						+ "5           Insert Coin\n"
						+ "";
				String[] htp = howToPlay.split("\n");
				for (int i = 0; i < htp.length; i++) {
					g.drawString(htp[i], 10, 10+g.getFontMetrics().getHeight()*(i+1));
				}
		}
		
		// Push Image to frame
		g.dispose();
		bs.show();
		while(paused) {
			Thread.yield();
		}
	}
}
