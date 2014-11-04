package de.hshannover.pp.slapemhard.resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Resource {
	public InputStream getInputStream(String relativePath) {
		if (this.getClass().getResource((">res>"+relativePath).replace(">", "/")) == null) {
			System.out.println("Resource \""+relativePath+"\" is not valid. Check if you updated package.");
			return null;
		}
		return this.getClass().getResourceAsStream((">res>"+relativePath).replace(">", "/"));//File.separator));
	}
}
