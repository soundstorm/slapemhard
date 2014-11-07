package de.hshannover.pp.slapemhard.images;

import java.awt.image.BufferedImage;

/**
 * 
 * @author SoundStorm
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
	 * Calls getTile(int y, int x)
	 * @param tile Number of the tile in SpriteSheet, numbered from left to right, top to bottom. 0-Indexed.
	 * @return The requested tile. Null if tile exceeds the number of tiles.
	 */
	public BufferedImage getTile(int tile) {
		return getTile(tile/cols, tile%cols);
	}
	/**
	 * 
	 * @param y 
	 * @param x 
	 * @return 
	 */
	public BufferedImage getTile(int y, int x) {
		if ((x >= cols)|(y >= rows)) {
			return null;
		}
		return bi.getSubimage(tileWidth*x, tileHeight*y, tileWidth, tileHeight);
	}
	public int getCols() {
		return cols;
	}
	public int getWidth() {
		return tileWidth;
	}
	public int getHeight() {
		return tileHeight;
	}
}