package de.hshannover.pp.slapemhard.images;

import java.awt.image.BufferedImage;

public class BufferedImageReference {
	private static BufferedImageLoader bL = new BufferedImageLoader();
	private String path;
	private BufferedImage bI;
	public BufferedImageReference(String path) {
		bI = bL.getImage(path);
		if (bI != null) {
			this.path = path;
		}
	}
	public BufferedImage getImage() {
		return bI;
	}
	public String getPath() {
		return path;
	}
}
