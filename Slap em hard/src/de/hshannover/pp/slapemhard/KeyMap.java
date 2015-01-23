package de.hshannover.pp.slapemhard;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class KeyMap {
	public static final int
		ESCAPE = 0,
		START = 1,
		COIN = 2,
		BUTTON1 = 3,
		BUTTON2 = 4,
		CANCEL = 4,
		BUTTON3 = 5,
		OK = 5,
		LEFT = 6,
		RIGHT = 7,
		UP = 8,
		DOWN = 9,
		EDITOR = 10;
	private final static int MAX_KEYS = 11;
	public static ArrayList<ArrayList<Integer>> keyCodes = new ArrayList<ArrayList<Integer>>(MAX_KEYS);
	
	public KeyMap() {
		for (int i = 0; i < MAX_KEYS; i++)
			keyCodes.add(new ArrayList<Integer>());
		keyCodes.get(START).add(KeyEvent.VK_1);
		keyCodes.get(COIN).add(KeyEvent.VK_5);
		keyCodes.get(BUTTON1).add(KeyEvent.VK_CONTROL);
		keyCodes.get(BUTTON1).add(KeyEvent.VK_SHIFT);
		keyCodes.get(BUTTON2).add(KeyEvent.VK_ALT);
		keyCodes.get(BUTTON2).add(KeyEvent.VK_Y);
		keyCodes.get(BUTTON3).add(KeyEvent.VK_SPACE);
		keyCodes.get(BUTTON3).add(KeyEvent.VK_X);
		keyCodes.get(OK).add(KeyEvent.VK_ENTER);
		keyCodes.get(LEFT).add(KeyEvent.VK_LEFT);
		keyCodes.get(RIGHT).add(KeyEvent.VK_RIGHT);
		keyCodes.get(UP).add(KeyEvent.VK_UP);
		keyCodes.get(DOWN).add(KeyEvent.VK_DOWN);
		keyCodes.get(ESCAPE).add(KeyEvent.VK_ESCAPE);
		keyCodes.get(EDITOR).add(KeyEvent.VK_TAB);
		keyCodes.get(EDITOR).add(KeyEvent.VK_F2);
	}
	
	
	public int getCode(int keyCode) {
		for (int i = 0; i < keyCodes.size(); i++) {
			if (keyCodes.get(i).contains(keyCode)) {
				return i;
			}
		}
		return -1;
	}
}
