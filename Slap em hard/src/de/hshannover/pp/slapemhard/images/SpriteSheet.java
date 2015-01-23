package de.hshannover.pp.slapemhard.images;

import java.awt.image.BufferedImage;

/**
 * A SpriteSheet merges many images or frames of a animation into one big image.
 * This image is processed by this class to get the subimages.
 * @author	Patrick Defayay<br />
 * 			Andre Schmidt<br />
 * 			Steffen Schulz<br />
 * 			Luca Zimmermann
 */

public class SpriteSheet {
	private BufferedImage bi;
	private int tileWidth, tileHeight, cols, rows;
	
	/**
	 * 
	 * @param bi BufferedImage, representing the SpriteSheet
	 * @param tileWidth Width of each tile
	 * @param tileHeight Height of each tile
	 */
	public SpriteSheet(BufferedImage bi, int tileWidth, int tileHeight) {
		this.bi = bi;
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
		cols = bi.getWidth()/tileWidth;
		rows = bi.getHeight()/tileHeight;
	}
	
	/**
	 * Calls {@link #getTile(int, int)} with corresponding values
	 * @param tile Number of the tile in SpriteSheet, numbered from left to right, top to bottom. 0-Indexed.
	 * @return The requested tile.
	 */
	public BufferedImage getTile(int tile) {
		return getTile(tile/cols, tile%cols);
	}
	/**
	 * Returns the requested tile of the SpriteSheet
	 * @param y Column of image
	 * @param x Row of image
	 * @return the requested tile of the SpriteSheet
	 */
	public BufferedImage getTile(int y, int x) {
		if ((x >= cols)|(y >= rows)) {
			return null;
		}
		return bi.getSubimage(tileWidth*x, tileHeight*y, tileWidth, tileHeight);
	}
	/**
	 * Returns the maximum columns available
	 * @return the maximum columns available
	 */
	public int getCols() {
		return cols;
	}
	public int getRows() {
		return rows;
	}
	/**
	 * Returns the width of each tile
	 * @return the width of each tile
	 */
	public int getWidth() {
		return tileWidth;
	}
	/**
	 * Returns the height of each tile
	 * @return the height of each tile
	 */
	public int getHeight() {
		return tileHeight;
	}
}