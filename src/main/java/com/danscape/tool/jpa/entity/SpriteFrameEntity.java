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
@Table(name="sprite_frames")
public class SpriteFrameEntity {
	@Id
	private int id;
	
	@Column(name="sprite_map_id")
	private int spriteMapId;
	
	private int x, y, w, h;
	
	@Column(name="anchor_x")
	private float anchorX;
	
	@Column(name="anchor_y")
	private float anchorY;
	
	@Column(name="scale_x")
	private float scaleX;
	
	@Column(name="scale_y")
	private float scaleY;
	
	private int margin;
	
	@Column(name="frame_count")
	private int frameCount;
	
	private int framerate;
	
	@Column(name="animation_type_id")
	private int animationTypeId;
}
