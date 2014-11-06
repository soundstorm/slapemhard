package de.hshannover.pp.slapemhard.threads;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import de.hshannover.pp.slapemhard.*;
import de.hshannover.pp.slapemhard.images.*;
import de.hshannover.pp.slapemhard.objects.*;

public class DrawLevelThread extends Canvas implements Runnable {
	private static final long serialVersionUID = 1L;
	boolean running;
	Level level;
	private Thread thread;
	int fps = 30;
	SpriteSheet lives;
	double scale;
	//public static int WIDTH, HEIGHT;
	private Dimension gameSize;
	
	public DrawLevelThread(Level level) {
		super();
		this.level = level;
		this.scale = level.getGame().getScale();
		this.gameSize = level.getGame().getGameSize();
		setIgnoreRepaint(true);
		lives = new SpriteSheet((new BufferedImageLoader()).getImage("images>lives.png"),11,11);
	}
	
	private void init(){
		/*WIDTH = game.getFrame().getWidth();
		HEIGHT = game.getFrame().getHeight();
		System.out.println(getWidth()+" "+getHeight());*/
	}
	
	public synchronized void start(){
		if(running){
			return;
		}
		running = true;
		thread = new Thread(this, "Rendering Thread");
		thread.start();
	}
	@Override
	public synchronized void run() {
		init();
		this.requestFocus();
		long lastTime = System.nanoTime();
		double amountOfTicks = 60.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		long timer = System.currentTimeMillis();
		int updates = 0;
		int frames = 0;
		//int waitDuration = 0;
		long waitDuration = 0L;
		while(running){
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			while(delta > 0){
				tick();
				updates++;
				delta--;
			}
			render();
			frames++;
					
			if(System.currentTimeMillis() - timer > 1000){
				timer += 1000;
				//System.out.println("FPS: " + frames + " TICKS: " + updates);
				fps = frames;
				frames = 0;
				updates = 0;
			}
			if (fps < 20) {
				//Don't wait if frame rate drops
				waitDuration = 0;
				System.out.println("FPS DROPPED BELOW 20!");
			} else {
				//33.3ms for 30fps. Reduced to 30ms to get a more constant framerate of 30fps, otherwise it will likely drop to 27fps.
				//Automatic compensation for elapsed time while rendering.
				//waitDuration = 30-(int)((System.nanoTime()-now)/1000000);
				waitDuration = 30000000-(System.nanoTime()-now);
				if (waitDuration < 0) {
					waitDuration = 0;
				}
			}
			try {
				//more precise if using a long instead:
				Thread.sleep((int)(waitDuration/1000000), (int)(waitDuration%1000000));
			} catch (InterruptedException e) {}
		}
	}
	
	private void tick(){
		for (int i = 0; i < level.getBullets().size(); i++) {
			Bullet obj = level.getBullets().get(i);
			obj.move();
			if (obj.isExploded() | obj.outOfWindow()) {
				level.getBullets().remove(obj);
				i--;
				continue;
			}
		}
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
		
		int xoffset = level.getPlayer().getPosition().x-100;
		if (xoffset < 0) {
			xoffset = 0;
		} else if (xoffset > level.getBounds().width-gameSize.width) {
			xoffset = level.getBounds().width-gameSize.width;
		}
		
		for (BufferedImage bI : level.getBackgroundImages()) {
			g.drawImage(bI, -xoffset*(bI.getWidth()-gameSize.width)/(level.getBounds().width-gameSize.width), 0, null);
		}
		
		//Move to active clip
		g2d.translate(-xoffset, 0);
		g.drawImage(level.getLandscapeImage(),0,0,null);

		for (CollisionObject ro : level.getCollisionObjects()) {
			ro.render(g);
		}
		
		for (int i = 0; i < level.getBullets().size(); i++) {
			Bullet obj = level.getBullets().get(i);
			obj.render(g);
		}
		
		for (Person ro : level.getEnemies()) {
			ro.render(g);
		}
		level.getPlayer().render(g);
		
		//
		g2d.translate(xoffset, 0);
		
		//Draw HUD
		g.setColor(Color.WHITE);
		g.fillRect(5,5,50,20);
		g.setColor(Color.BLACK);
		g.setFont(level.getFont().deriveFont(Font.PLAIN,8));
		g.drawString("BULLETS: "+level.getPlayer().getWeapon().getAmmo(), 10, 13);
		g.drawString("FPS: "+fps, 10, 20);
		for (int i=0; i < level.getPlayer().getLives(); i++) {
			if (i != level.getPlayer().getLives()-1) {
				g.drawImage(lives.getTile(9), 60+i*12, 10, null);
			} else {
				g.drawImage(lives.getTile(9*level.getPlayer().getHealth()/level.getPlayer().getMaxHealth()), 60+i*12, 10, null);
			}
		}
		
		// Push Image to frame
		g.dispose();
		bs.show();
	}
}
