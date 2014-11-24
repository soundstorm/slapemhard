package de.hshannover.pp.slapemhard.threads;


import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.logging.Logger;

import de.hshannover.pp.slapemhard.*;
import de.hshannover.pp.slapemhard.images.*;
import de.hshannover.pp.slapemhard.objects.*;

public class DrawThread extends Canvas implements Runnable {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(DrawThread.class.getName());
	
	private boolean running;
	private Menu menu;
	private Thread thread;
	private int fps = 30;
	private static SpriteSheet lives = new SpriteSheet((new BufferedImageLoader()).getImage("images/lives.png"),11,11);
	private double scale;
	private Dimension gameSize;
	
	public DrawThread(Menu menu) {
		super();
		this.menu = menu;
		this.scale = menu.getScale();
		this.gameSize = menu.getGameSize();
		setIgnoreRepaint(true);
	}
	
	private void init(){
	}
	
	private void move() {
		menu.getGame().getPlayer().move();
		for (int i = 0; i < menu.getGame().getBullets().size(); i++) {
			try {
				Bullet obj = menu.getGame().getBullets().get(i);
				obj.move();
				if (obj.isExploded()) {
					menu.getGame().getBullets().remove(obj);
					i--;
				}
			} catch (Exception e) {
				log.warning("Cant modify Bullet:\n"+e.toString());
			}
		}
		for (int i = 0; i < menu.getGame().getEnemies().size(); i++) {
			try {
				Person obj = menu.getGame().getEnemies().get(i);
				obj.move();
				if (!obj.isAlive()) {
					//obj.stop();
					menu.getGame().addPoints(obj.getPower()*40);
					menu.getGame().getPowerUps().add(new PowerUp(menu.getGame(),new Dimension(obj.x,obj.y+30),1));
					menu.getGame().getEnemies().remove(obj);
					i--;
					//continue;
				}
			} catch (Exception e) {
				log.warning("Cant modify Bullet:\n"+e.toString());
			}
		}
	}
	
	public synchronized void start(){
		if(running){
			System.out.println("Draw Thread alread running");
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
		double amountOfTicks = 180.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		long timer = System.currentTimeMillis();
		int frames = 0;
		long waitDuration = 0L;
		int ticks = 0;
		while(running){
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			while(delta > 0) {
				//tick();
				if (menu.getLevel() != null)
					move();
				delta--;
				ticks++;
			}
			render();
			frames++;
					
			if(System.currentTimeMillis() - timer > 1000){
				timer += 1000;
				fps = frames;
				frames = 0;
				System.out.println("Ticks: "+ticks);
				ticks = 0;
			}
			if (fps < 20) {
				//Don't wait if frame rate drops
				//System.out.println("FPS DROPPED BELOW 20!");
				continue;
			} else {
				//33.3ms for 30fps. Reduced to 30ms to get a more constant framerate of 30fps, otherwise it will likely drop to 27fps.
				//Automatic compensation for elapsed time while rendering.
				waitDuration = 30000000-(System.nanoTime()-now);
				if (waitDuration < 0) {
					waitDuration = 0;
				}
			}
			try {
				Thread.sleep((int)(waitDuration/1000000), (int)(waitDuration%1000000));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if(bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		
		Graphics g = bs.getDrawGraphics();
		Graphics2D g2d = (Graphics2D) g;
		//Scale content to fit screen
		g2d.scale(scale, scale);
		//Render corresponding content
		if (menu.getGame() != null) {
			if (menu.getLevel() != null) {
				render(menu.getLevel(),g,g2d);
			} else {
				menu.getGame().render(g);
			}
		} else {
			menu.render(g);
		}
		
		// Push Image to frame
		g.dispose();
		bs.show();
	}

	private void render(Level level, Graphics g, Graphics2D g2d) {
		try {
			Rectangle activePosition = level.getPlayer();// = new Dimension(level.getPlayer().x,level.getPlayer().y);
			
			int xoffset = level.getPlayer().x-100;
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
			
			
			for (int i = 0; i < level.getBullets().size(); i++) {
				try {
					Bullet obj =  level.getBullets().get(i);
					obj.render(g);
				} catch (Exception e) {
					System.out.println("Cant render Bullet");
				}
			}
			
			for (int i = 0; i < level.getPowerUps().size(); i++) {
				try {
					PowerUp obj =  level.getPowerUps().get(i);
					obj.render(g);
				} catch (Exception e) {
					System.out.println("Cant render PowerUp");
				}
			}
			
			for (int i = 0; i < level.getEnemies().size(); i++) {
				try {
					Person obj =  level.getEnemies().get(i);
					//if (obj.getPosition().x > xoffset-100 | obj.getPosition().x+obj.getPosition().width < xoffset+220)
						obj.render(g);
				} catch (Exception e) {
					System.out.println("Cant render Enemy");
				}
			}
			//Render at position previously determined
			//Prevents shaking, if Player moved in the meantime of rendering
			level.getPlayer().render(g,activePosition.x,activePosition.y);
			
			//
			g2d.translate(xoffset, 0);
			
			for (BufferedImage fI : level.getForegroundImages()) {
				g.drawImage(fI, -xoffset*(fI.getWidth()-gameSize.width)/(level.getBounds().width-gameSize.width), 0, null);
			}
			
			/*for (CollisionObject ro : level.getCollisionObjects()) {
				ro.render(g);
			}*/
			
			//Draw HUD
			g.setColor(new Color(255, 255, 255, 127));
			g.fillRect(5,5,50,40);
			g.setColor(Color.BLACK);
			g.setFont(level.getFont().deriveFont(Font.PLAIN,8));
			g.drawString("AMMO: "+level.getPlayer().getWeapon().getAmmo(), 10, 13);
			g.drawString("FPS:  "+fps, 10, 20);
			g.drawString("COIN: "+menu.getGame().getCoins(), 10, 27);
			g.drawString("PTS:  "+menu.getGame().getPoints(), 10, 34);
			long timeRemaining = level.getRemainingTime();
			g.drawString(""+timeRemaining, 50-(""+timeRemaining).length()*5, 41);
			for (int i=0; i < level.getPlayer().getLives(); i++) {
				if (i != level.getPlayer().getLives()-1) {
					g.drawImage(lives.getTile(9), 60+i*12, 10, null);
				} else {
					if (level.getPlayer().isInvincible()) {
						g.drawImage(lives.getTile(10), 60+i*12, 10, null);
					} else {
						g.drawImage(lives.getTile(9*level.getPlayer().getHealth()/level.getPlayer().getMaxHealth()), 60+i*12, 10, null);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void interrupt() {
		thread.interrupt();
	}
}
