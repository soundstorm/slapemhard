package de.hshannover.pp.slapemhard.images;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import de.hshannover.pp.slapemhard.resources.Resource;

public class BufferedImageLoader {
	Resource r;
	public BufferedImageLoader() {
		r = new Resource();
	}
	public BufferedImage getImage(String relPath) {
		try {
			BufferedImage bi = ImageIO.read(r.getInputStream(relPath));
			return bi;
		} catch (IOException e) {
			System.out.println("NULL");
			return null;
		}
	}
}
