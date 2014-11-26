package de.hshannover.pp.slapemhard.resources;
import java.io.FileInputStream;
import java.io.InputStream;

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
		if (this.getClass().getResource("/res/"+relativePath) == null) {
			System.out.println("Resource \""+relativePath+"\" is not valid. Check if you updated package.");
			return null;
		}
		return this.getClass().getResourceAsStream("/res/"+relativePath);
	}
}