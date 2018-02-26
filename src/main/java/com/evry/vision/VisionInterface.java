package com.evry.vision;

import java.io.FileInputStream;

import com.google.gson.JsonElement;

public interface VisionInterface {
	
	public int initialize();
	
	public String classifyImages(String imageName, FileInputStream stream);
	
	public String classifyImages(String imageName, byte[] stream);

}
