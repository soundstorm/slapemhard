package de.hshannover.pp.slapemhard.threads;


import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;

import de.hshannover.pp.slapemhard.Menu;
/**
 * @author	Patrick Defayay<br />
 * 			Andre Schmidt<br />
 * 			Steffen Schulz<br />
 * 			Luca Zimmermann
 */
public class DrawThread extends Canvas implements Runnable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3840795507744337763L;
	private boolean running;
	private Menu menu;
	private Thread thread;
	private int fps = 30;
	private double scale;
	public static final int amountOfTicks = 180;
	
	public DrawThread(Menu menu) {
		super();
		this.menu = menu;
		this.scale = menu.getScale();
		setIgnoreRepaint(true);
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
		this.requestFocus();
		long lastTime = System.nanoTime();
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		long timer = System.currentTimeMillis();
		int frames = 0;
		long waitDuration = 0L;
		@SuppressWarnings("unused")
		int ticks = 0;
		while(running){
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			while(delta > 0) {
				//tick();
				menu.tick();
				delta--;
				ticks++;
			}
			render();
			frames++;
					
			if(System.currentTimeMillis() - timer > 1000){
				timer += 1000;
				fps = frames;
				frames = 0;
				//System.out.println("Ticks: "+ticks);
				//System.out.println("FPS:   "+fps);
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
		//Render content
		menu.render(g);
		// Push Image to frame
		g.dispose();
		bs.show();
	}

	public void interrupt() {
		thread.interrupt();
	}
}
