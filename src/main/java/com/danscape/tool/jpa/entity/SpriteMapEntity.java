package com.danscape.tool.jpa.entity;

import java.sql.Blob;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name="sprite_maps")
public class SpriteMapEntity {
	@Id
	private int id;
	private String name;
	private Blob data;
}
