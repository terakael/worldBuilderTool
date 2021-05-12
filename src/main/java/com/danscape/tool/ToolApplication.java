package com.danscape.tool;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ToolApplication implements CommandLineRunner {
	@Autowired
	private Swing swing;

	@Autowired
	private MinimapGenerator generator;
	
	public static void main(String[] args) {
		SpringApplication.run(ToolApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		if (args.length == 0) {
			swing.go();
		} else if (args[0].equals("generateMinimap")) {
			// [0] generateMinimap
			// fX floor
			// sX segment
			
			Integer floor = null;
			Integer segment = null;
			boolean generateFullMap = false;
			for (int i = 1; i < args.length; ++i) {
				switch (args[i].charAt(0)) {
				case 'f': { // floor
					floor = Integer.parseInt(args[i].substring(1));
					break;
				}
				case 's': { // segment
					segment = Integer.parseInt(args[i].substring(1));
					break;
				}
				}
				
				if (args[i].equals("generateFullMap"))
					generateFullMap = true;
			}
			generator.generate(floor, segment, generateFullMap);
		}
	}

}
