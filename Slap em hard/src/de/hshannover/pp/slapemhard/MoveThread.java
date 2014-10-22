package de.hshannover.pp.slapemhard;

import java.util.ArrayList;

import de.hshannover.pp.slapemhard.objects.*;

public class MoveThread extends Thread {
	private boolean moveLeft,moveRight,jump,fire;
	int jumped;
	Player me;
	ArrayList<CollisionObject> collisionObjects;
	public MoveThread(Player me, ArrayList<CollisionObject> collisionObjects) {
		this.me = me;
		this.collisionObjects = collisionObjects;
	}
	@Override
	public synchronized void run() {
		while (true) {
			if (moveLeft) {
				me.move(-1, jump?1:-1, collisionObjects);
			} else
			if (moveRight) {
				me.move(1, jump?1:-1, collisionObjects);
			} else {
				boolean[] collision = me.move(0, jump?1:-1, collisionObjects); //Gravity
				if (jump && (collision[1] | jumped >= 40)) { //Collision with object above or reached max jump height
					jump = false;
					jumped = 0;
				} else if (jump) {//Jumped less than max jump height
					jumped++;
				}
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
