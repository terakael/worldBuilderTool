package com.danscape.tool.jpa.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Table(name = "room_ground_textures")
@IdClass(RoomGroundTextureCompositeKey.class)
public class RoomGroundTextureEntity {
	@Id
	@Column(name="room_id")
	private int roomId;
	
	@Id
	@Column(name="tile_id")
	private int tileId;
	
	@Column(name="ground_texture_id")
	private int groundTextureId;
}
