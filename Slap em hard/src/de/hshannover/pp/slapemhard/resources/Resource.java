package de.hshannover.pp.slapemhard.resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Resource {
	public InputStream getInputStream(String relativePath) {
		try {
			System.out.println(this.getClass().getResource((">res>"+relativePath).replace(">", File.separator)).toString());
		} catch (NullPointerException e) {
			try {
				System.out.println(this.getClass().getResource((">res>"+relativePath).replace(">", "/")).toString());
			} catch (NullPointerException i) {
				System.out.println("NULL");
			}
		}
		return this.getClass().getResourceAsStream((">res>"+relativePath).replace(">", "/"));//File.separator));
	}
}
