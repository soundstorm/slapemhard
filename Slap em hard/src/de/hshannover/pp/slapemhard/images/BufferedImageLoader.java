package de.hshannover.pp.slapemhard.images;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import de.hshannover.pp.slapemhard.resources.Resource;

/**
 * Loads and caches Images as BufferedImage from given relative path.
 * @author	Patrick Defayay<br />
 * 			Andre Schmidt<br />
 * 			Steffen Schulz<br />
 * 			Luca Zimmermann
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
	public BufferedImage getImage(String path, boolean absolute) {
		int index = paths.indexOf(path);
		if (index >= 0) {
			//Image is already cached
			System.out.println("Got cached image "+path);
			return images.get(index);
		}
		try {
			BufferedImage bi;
			if (absolute)
				bi = ImageIO.read(r.getAbsoluteInputStream(path));
			else
				bi = ImageIO.read(r.getInputStream(path));
			//add to cache
			paths.add(path);
			images.add(bi);
			return bi;
		} catch (Exception e) {
			return null;
		}
	}
	public BufferedImage getImage(String relPath) {
		return getImage(relPath,false);
	}
}
