package de.hshannover.pp.slapemhard.resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Resource {
	public InputStream getInputStream(String relativePath) {
		return this.getClass().getResourceAsStream((">res>"+relativePath).replace(">", File.separator));
	}
}
