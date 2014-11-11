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
	
	
	/*public void interrupt() {
		running = false;
		for (Person p : level.getEnemies()) {
			p.stop();
		}
		//thread.interrupt();
	}*/
	
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
		//long lastTime = System.nanoTime();
		//double amountOfTicks = 60.0;
		//double ns = 1000000000 / amountOfTicks;
		//double delta = 0;
		long timer = System.currentTimeMillis();
		int frames = 0;
		//int waitDuration = 0;
		long waitDuration = 0L;
		while(running){
			long now = System.nanoTime();
			//delta += (now - lastTime) / ns;
			//lastTime = now;
			//while(delta > 0) {
				if (menu.getLevel() != null) {
					tick(menu.getLevel());
				}
			//	delta--;
			//}
			render();
			frames++;
					
			if(System.currentTimeMillis() - timer > 1000){
				timer += 1000;
				fps = frames;
				frames = 0;
			}
			if (fps < 20) {
				//Don't wait if frame rate drops
				//System.out.println("FPS DROPPED BELOW 20!");
				continue;
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
				//more precise than int:
				Thread.sleep((int)(waitDuration/1000000), (int)(waitDuration%1000000));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void tick(Level level){
		try {
			for (int i = 0; i < level.getBullets().size(); i++) {
				Bullet obj = level.getBullets().get(i);
				obj.move();
				boolean[] oow = obj.outOfWindow();
				if (obj.isExploded() | oow[0] | (!obj.getGravity() && oow[1]) | oow[2] | oow[3]) {
					level.getBullets().remove(obj);
					i--;
					//continue;
				}
			}
			for (int i = 0; i < level.getEnemies().size(); i++) {
				Person obj = level.getEnemies().get(i);
				if (!obj.isAlive()) {
					obj.stop();
					level.getEnemies().remove(obj);
					i--;
					//continue;
				}
			}
		} catch (NullPointerException e) {
			log.warning(e.toString());
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
		Rectangle activePosition = level.getPlayer().getPosition();
		
		int xoffset = activePosition.x-100;
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

		/*for (CollisionObject ro : level.getCollisionObjects()) {
			ro.render(g);
		}*/
		
		for (int i = 0; i < level.getBullets().size(); i++) {
			try {
				Bullet obj =  level.getBullets().get(i);
				//if (obj.getPosition().x > xoffset-100 | obj.getPosition().x+obj.getPosition().width < xoffset+220)
					obj.render(g);
			} catch (Exception e) {
				System.out.println("Cant render Bullet");
			}
		}
		
		for (int i = 0; i < level.getPowerUps().size(); i++) {
			try {
				PowerUp obj =  level.getPowerUps().get(i);
				//if (obj.getPosition().x > xoffset-100 | obj.getPosition().x+obj.getPosition().width < xoffset+220)
					obj.render(g);
			} catch (Exception e) {
				System.out.println("Cant render Powerup");
			}
		}
		
		for (int i = 0; i < level.getEnemies().size(); i++) {
			try {
				Person obj =  level.getEnemies().get(i);
				//if (obj.getPosition().x > xoffset-100 | obj.getPosition().x+obj.getPosition().width < xoffset+220)
					obj.render(g);
			} catch (Exception e) {}
		}
		//Render at position previously determined
		//Prevents shaking, if Player moved in the meantime of rendering
		level.getPlayer().render(g,activePosition.x,activePosition.y);
		
		//
		g2d.translate(xoffset, 0);
		
		for (BufferedImage fI : level.getForegroundImages()) {
			g.drawImage(fI, -xoffset*(fI.getWidth()-gameSize.width)/(level.getBounds().width-gameSize.width), 0, null);
		}
		
		//Draw HUD
		g.setColor(new Color(255, 255, 255, 127));
		g.fillRect(5,5,50,40);
		g.setColor(Color.BLACK);
		g.setFont(level.getFont().deriveFont(Font.PLAIN,8));
		g.drawString("BULLETS: "+level.getPlayer().getWeapon().getAmmo(), 10, 13);
		g.drawString("FPS: "+fps, 10, 20);
		long timeRemaining = level.getRemainingTime();
		g.drawString(""+timeRemaining, 50-(""+timeRemaining).length()*4, 27);
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
	}

	public void interrupt() {
		thread.interrupt();
	}
}
