package com.danscape.tool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ToolApplication implements CommandLineRunner {
	@Autowired
	private Swing swing;

	public static void main(String[] args) {
		SpringApplication.run(ToolApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		swing.go();
	}

}
