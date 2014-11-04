package de.hshannover.pp.slapemhard.images;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class BufferedImageLoader {
	public BufferedImage getImage(String relPath) {
		relPath = relPath.replaceAll(">", File.separator);
		try {
			File f = new File(relPath);
			BufferedImage bi = ImageIO.read(f);
			return bi;
		} catch (IOException e) {
			System.out.println("NULL");
			return null;
		}
	}
}
