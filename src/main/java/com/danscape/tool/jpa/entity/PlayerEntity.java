package com.danscape.tool.jpa.entity;

import java.util.Date;

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
@Table(name="player")
public class PlayerEntity {
	@Id
	private int id;
	
	private String name;
	
	@Transient
	private String password;
	
	@Transient
	@Column(name="password_salt")
	private String passwordSalt;
	
	@Transient
	@Column(name="last_logged_in")
	private Date lastLoggedIn;
	
	@Column(name="tile_id")
	private int tileId;
	
	@Transient
	@Column(name="attack_style_id")
	private int attackStyleId;
	private int floor;
}
