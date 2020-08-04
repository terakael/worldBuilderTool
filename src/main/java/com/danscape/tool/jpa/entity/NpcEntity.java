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
@Table(name="npcs")
public class NpcEntity {
	@Id
	private int id;
	
	private String name;
	
	@Transient
	private String description;
	
	@Transient
	@Column(name="up_id")
	private int upId;
	
	@Column(name="down_id")
	private int downId;
	
	@Transient
	@Column(name="left_id")
	private int leftId;
	
	@Transient
	@Column(name="right_id")
	private int rightId;
	
	@Transient
	@Column(name="attack_id")
	private int attackId;
	
	@Transient
	@Column(name="scale_x")
	private float scaleX;
	
	@Transient
	@Column(name="scale_y")
	private float scaleY;
	
	@Transient	
	private int acc, str, def, agil, hp, magic;
	
	@Transient
	@Column(name="acc_bonus")
	private int accBonus;
	
	@Transient
	@Column(name="str_bonus")
	private int strBonus;
	
	@Transient
	@Column(name="def_bonus")
	private int defBonus;
	
	@Transient
	@Column(name="agil_bonus")
	private int agilBonus;
	
	@Transient
	@Column(name="attack_speed")
	private int attackSpeed;
	
	@Transient
	@Column(name="leftclick_option")
	private int leftclickOption;
	
	@Transient
	@Column(name="other_options")
	private int otherOptions;
	
	@Transient
	@Column(name="roam_radius")
	private int roamRadius;
	
	@Transient
	private int attriutes;
	
	@Transient
	@Column(name="respawn_ticks")
	private int respawnTicks;
}
