package com.danscape.tool.jpa.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name="scenery")
public class SceneryEntity {
	@Id
	private int id;
	
	private String name;
	
	@Column(name="sprite_map_id")
	private int spriteMapId;
	
	private int x;
	
	private int y;
	
	private int w;
	
	private int h;
	
	
	// below values not used
	@Transient
	private String examine;
	
	@Transient
	@Column(name="anchor_x")
	private float anchorX;
	
	@Transient
	@Column(name="anchor_x")
	private float anchorY;
	
	@Transient
	@Column(name="framecount")
	private int frameCount;
	
	@Transient
	private int frameRate;
	
	@Transient
	@Column(name="leftclick_option")
	private int leftclickOption;
	
	@Transient
	@Column(name="other_options")
	private int otherOptions;
	
	@Transient
	private int impassable;
}
