package de.hshannover.pp.slapemhard.threads;

import java.util.ArrayList;

import de.hshannover.pp.slapemhard.*;
import de.hshannover.pp.slapemhard.objects.*;

public class MoveThread extends Thread {
	private boolean moveLeft,moveRight,jump,fire;
	int jumped;
	SlapEmHard game;
	public MoveThread(SlapEmHard game) {
		this.game = game;
	}
	@Override
	public synchronized void run() {
		while (true) {
			if (moveLeft) {
				game.getPlayer().move(-1, 0, game.getCollisionObjects());
			} else
			if (moveRight) {
				game.getPlayer().move(1, 0, game.getCollisionObjects());
			}
			boolean[] collision = game.getPlayer().move(0, jump?1:-1, game.getCollisionObjects()); //Gravity
			if (jump && (collision[1] | jumped >= 40)) { //Collision with object above or reached max jump height
				jump = false;
				jumped = 0;
			} else if (jump) {//Jumped less than max jump height
				jumped++;
			}
			try {
				sleep(15);
			} catch (InterruptedException e) {}
		}
	}
	public void setLeft(boolean b) {moveLeft=b;}
	public void setRight(boolean b) {moveRight=b;}
	public void setJump(boolean b) {jump=b; jumped = 0;}
}
