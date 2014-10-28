package de.hshannover.pp.slapemhard.threads;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import de.hshannover.pp.slapemhard.*;
import de.hshannover.pp.slapemhard.objects.*;

public class DrawGameThread extends Thread {
	boolean running;
	SlapEmHard game;
	public DrawGameThread(SlapEmHard game) {
		this.game = game;
	}
	@Override
	public void start() {
		running = true;
		super.start();
	}
	@Override
	public synchronized void run() {
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
}
