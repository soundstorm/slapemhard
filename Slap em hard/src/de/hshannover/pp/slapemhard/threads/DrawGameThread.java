package de.hshannover.pp.slapemhard.threads;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;

import de.hshannover.pp.slapemhard.*;
import de.hshannover.pp.slapemhard.objects.*;

public class DrawGameThread extends Canvas implements Runnable {
	private static final long serialVersionUID = 1L;
	boolean running;
	SlapEmHard game;
	private Thread thread;
	int fps = 30;
	//public static int WIDTH, HEIGHT;
	
	public DrawGameThread(SlapEmHard game) {
		super();
		this.game = game;
		setIgnoreRepaint(true);
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
		int waitDuration = 0;
		//int waitNanos = 0;
		while(running){
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			while(delta >= 1){
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
				//33.3ms for 30fps. Reduced to 29ms to get a more constant framerate of 30fps, otherwise it will likely drop to 27fps.
				//Automatic compensation for elapsed time while rendering.
				waitDuration = 29-(int)((System.nanoTime()-now)/1000000);
				if (waitDuration < 0) {
					waitDuration = 0;
				}
			}
			try {
				Thread.sleep(waitDuration);
				//Thread.sleep((int)(waitDuration/1000000), (int)(waitDuration%1000000));
			} catch (InterruptedException e) {}
		}
	}
	
	private void tick(){
		/*handler.tick();
		for(int i = 0; i < handler.object.size(); i++){
			if(handler.object.get(i).getId() == ObjectId.Player){
				cam.tick(handler.object.get(i));
			}
		}*/
	}
	
	private void render(){
		BufferStrategy bs = this.getBufferStrategy();
		if(bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		
		Graphics g = bs.getDrawGraphics();
		Graphics2D g2d = (Graphics2D) g;
		g2d.scale(2.0, 2.0);
		////////////////////////////////////
		
		g.setColor(Color.GRAY);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		int xoffset = game.getPlayer().getPosition().x-100;
		if (xoffset < 0) {
			xoffset = 0;
		}
		//Move to active clip
		g2d.translate(-xoffset, 0);

		for (CollisionObject ro : game.getCollisionObjects()) {
			ro.render(g);
		}
		
		for (int i = 0; i < game.getBullets().size(); i++) {
			Bullet obj = game.getBullets().get(i);
			if (obj.isExploded() | obj.outOfWindow()) {
				game.getBullets().remove(obj);
				i--;
				continue;
			}
			obj.move();
			obj.render(g);
		}
		
		for (Person ro : game.getEnemies()) {
			ro.render(g);
		}
		game.getPlayer().render(g);
		
		//
		g2d.translate(xoffset, 0);
		
		//Draw HUD
		g.setColor(Color.WHITE);
		g.fillRect(5,5,50,20);
		g.setColor(Color.BLACK);
		g.setFont(game.getFont().deriveFont(Font.PLAIN,8));
		g.drawString("BULLETS: "+game.getPlayer().getWeapon().getAmmo(), 10, 13);
		g.drawString("FPS: "+fps, 10, 20);
		
		// Push Image to frame
		g.dispose();
		bs.show();
	}
}
