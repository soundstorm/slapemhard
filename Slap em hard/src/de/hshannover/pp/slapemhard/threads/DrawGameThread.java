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
		thread = new Thread(this);
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
		int staticFrames = 30;
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
				System.out.println("FPS: " + frames + " TICKS: " + updates);
				staticFrames = frames;
				frames = 0;
				updates = 0;
			}
			if (staticFrames < 20) {
				waitDuration = 0;
				//waitNanos = 0;
			} else {
				waitDuration = 30-(int)(System.nanoTime()-now)/10000000;
				//waitNanos = (int)((System.nanoTime()-now))%10000000;
				//System.out.println(waitNanos);
				
			}
			try {
				Thread.sleep(waitDuration);
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
		g2d.translate(-xoffset, 0); //Beginn der Kamera
		
		//handler.render(g);
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
		/*for (Bullet ro : game.getBullets()) {
			ro.render(g);
		}*/
		for (Person ro : game.getEnemies()) {
			ro.render(g);
		}
		for (CollisionObject ro : game.getCollisionObjects()) {
			ro.render(g);
		}
		game.getPlayer().render(g);
		
		g2d.translate(xoffset, 0);
		
		//Draw HUD
		g.setColor(Color.WHITE);
		g.fillRect(5,5,50,10);
		g.setColor(Color.BLACK);
		g.setFont(game.getFont().deriveFont(Font.PLAIN,8));
		g.drawString("BULLETS: "+game.getPlayer().getWeapon().getAmmo(), 10, 13);
		
		////////////////////////////////////
		g.dispose();
		bs.show();
	}
	
	/*
	foobar {
		while (running) {
			int xoffset = game.getPlayer().getPosition().x-100;
			if (xoffset < 0) {
				xoffset = 0;
			}
			game.getGraphics().setColor(Color.GREEN);
			game.getGraphics().fillRect(0, 0, game.getFrame().getWidth(), game.getFrame().getHeight());
			game.getGraphics().setColor(Color.RED);
			for (Person obj : game.getEnemies()) {
				game.getGraphics().drawRect(obj.getPosition().x-xoffset, obj.getPosition().y, obj.getPosition().width, obj.getPosition().height);
			}
			game.getGraphics().setColor(Color.WHITE);
			System.out.println(game.getBullets().size());
			for (int i = 0; i < game.getBullets().size(); i++) {
				Bullet obj = game.getBullets().get(i);
				if (obj.isExploded()) {
					game.getBullets().remove(obj);
					System.out.println("REMOVED");
					i--;
				} else {
					//AffineTransform at = new AffineTransform();
					//at.translate(game.getFrame().getWidth()/2, game.getFrame().getHeight() / 2);
					//at.rotate(-obj.getAngle());
					//at.translate(-game.getFrame().getWidth()/2, -game.getFrame().getHeight() / 2);
					//at.translate(obj.getPosition().x-xoffset, obj.getPosition().y);
					//((Graphics2D)game.getGraphics()).drawImage(obj.getImage(), at, null);
					game.getGraphics().drawImage(obj.getImage(), obj.getPosition().x-xoffset, obj.getPosition().y, null);
					obj.move();
				}
			}
			game.getGraphics().setColor(Color.YELLOW);
			for (CollisionObject obj : game.getCollisionObjects()) {
				game.getGraphics().fillRect(obj.getPosition().x-xoffset, obj.getPosition().y, obj.getPosition().width, obj.getPosition().height);
				//System.out.println(obj.getPosition().y);
			}
			game.getGraphics().setColor(Color.GREEN);
			//graphics.drawRect(me.getPosition().x-xoffset, me.getPosition().y, me.getPosition().width, me.getPosition().height);
			game.getGraphics().drawImage(game.getPlayer().getImage(),game.getPlayer().getPosition().x-xoffset, game.getPlayer().getPosition().y,null);
			//game.getBullets().get(0).move();
			try {
				sleep(10);
			} catch (InterruptedException e) {
				
			}
		}
	}
	*/
}
