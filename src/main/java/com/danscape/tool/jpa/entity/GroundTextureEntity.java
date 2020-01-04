package com.danscape.tool.jpa.entity;

import javax.persistence.Column;
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
@Table(name="ground_textures")
public class GroundTextureEntity {
	@Id
	private int id;
	
	@Column(name="sprite_map_id")
	private int spriteMapId;
	
	private int x;
	private int y;
}
