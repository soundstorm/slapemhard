package de.hshannover.pp.slapemhard.images;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import de.hshannover.pp.slapemhard.resources.Resource;

/**
 * Loads and caches Images as BufferedImage from given relative path.
 * @author SoundStorm
 *
 */
public class BufferedImageLoader {
	private static Resource r = new Resource();
	private static ArrayList<String> paths = new ArrayList<String>();
	private static ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();
	/**
	 * Loads and caches Images as BufferedImage from given relative path.
	 * @param relPath Relative path to image
	 * @return {@link BufferedImage} created from path or cached
	 */
	public BufferedImage getImage(String relPath) {
		int index = paths.indexOf(relPath);
		if (index >= 0) {
			//Image is already cached
			System.out.println("Got cached image "+relPath);
			return images.get(index);
		}
		try {
			BufferedImage bi = ImageIO.read(r.getInputStream(relPath));
			//add to cache
			paths.add(relPath);
			images.add(bi);
			return bi;
		} catch (IOException e) {
			return null;
		}
	}
}
