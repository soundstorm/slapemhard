package de.hshannover.pp.slapemhard.threads;

import de.hshannover.pp.slapemhard.*;

public class MoveThread extends Thread {
	private boolean moveLeft,moveRight,jump,fire;
	int jumped;
	Level level;
	public MoveThread(Level level) {
		super("Moving Thread");
		this.level = level;
	}
	@Override
	public synchronized void run() {
		while (true) {
			if (moveLeft) {
				level.getPlayer().move(-1, 0, level.getCollisionObjects());
			} else
			if (moveRight) {
				level.getPlayer().move(1, 0, level.getCollisionObjects());
			}
			if (moveRight | moveLeft) {
				level.getPlayer().setWalking(true);
			} else {
				level.getPlayer().setWalking(false);
			}
			boolean[] collision = level.getPlayer().move(0, jump?1:-1, level.getCollisionObjects()); //Gravity
			if (jump | !collision[1]) {
				level.getPlayer().setJumping(true);
			} else {
				level.getPlayer().setJumping(false);
			}
			if (jump && (collision[1] | jumped >= 70)) { //Collision with object above or reached max jump height
				jump = false;
				jumped = 0;
			} else if (jump) {//Jumped less than max jump height
				jumped++;
			}
			try {
				sleep(5);
			} catch (InterruptedException e) {}
		}
	}
	public void setLeft(boolean b) {moveLeft=b;}
	public void setRight(boolean b) {moveRight=b;}
	public void setJump(boolean b) {jump=b; jumped = 0;}
}
