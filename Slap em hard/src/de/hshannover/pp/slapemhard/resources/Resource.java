package de.hshannover.pp.slapemhard.resources;
import java.io.InputStream;

public class Resource {
	public InputStream getInputStream(String relativePath) {
		if (this.getClass().getResource("/res/"+relativePath) == null) {
			System.out.println("Resource \""+relativePath+"\" is not valid. Check if you updated package.");
			return null;
		}
		return this.getClass().getResourceAsStream("/res/"+relativePath);
	}
}
