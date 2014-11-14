package de.hshannover.pp.slapemhard;

import java.util.ArrayList;

public class Highscore implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	private ArrayList<String> names;
	private ArrayList<Integer> scores;
	
	public Highscore(){
		names = new ArrayList<String>();
		scores = new ArrayList<Integer>();
	}
	public int addHighscore(String name, int score) {
		int pos = 0;
		for (int i = scores.size()-1; i >= 0; i--) {
			if (scores.get(i) < score)
				continue;
			pos = i+1;
			break;
		}
		if (pos == 8)
			return -1;
		scores.add(pos,score);
		names.add(pos,name);
		if (scores.size() == 9) {
			scores.remove(8);
			names.remove(8);
		}
		return pos;
	}
	
	public int getScore(int pos) {
		try {
			return scores.get(pos);
		} catch (IndexOutOfBoundsException e) {
			return 0;
		}
	}
	
	public String getName(int pos) {
		try {
			return names.get(pos);
		} catch (IndexOutOfBoundsException e) {
			return "----";
		}
	}
}
