package de.hshannover.pp.slapemhard.resources;
import java.io.FileInputStream;
import java.io.InputStream;

import de.hshannover.pp.slapemhard.SlapEmHard;
/**
 * @author	Patrick Defayay<br />
 * 			Andre Schmidt<br />
 * 			Steffen Schulz<br />
 * 			Luca Zimmermann
 */
public class Resource {
	public InputStream getAbsoluteInputStream(String absolutePath) {
		try {
			return new FileInputStream(absolutePath);
		} catch (Exception e) {
			System.out.println("Resource \""+absolutePath+"\" is not valid.");
			return null;
		}
		
	}
	
	public InputStream getInputStream(String relativePath) {
		if (relativePath.startsWith("/")) {
			System.out.println("Shorting out leading \"/\"");
			relativePath = relativePath.substring(1);
		}
		if (SlapEmHard.class.getResource("/res/"+relativePath) == null) {
			System.out.println("Resource \"/res/"+relativePath+"\" is not valid. Check if you updated package.");
			return null;
		}
		return SlapEmHard.class.getResourceAsStream("/res/"+relativePath);
	}
}