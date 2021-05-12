package com.danscape.tool;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {
	@Bean
	public Swing swing() {
		return new Swing();
	}
	
	public static int MAP_ROW_TILE_LENGTH = 46325;
}
