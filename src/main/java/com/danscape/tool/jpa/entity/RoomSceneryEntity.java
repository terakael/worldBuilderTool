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
@Table(name = "room_scenery")
@IdClass(RoomSceneryCompositeKey.class)
public class RoomSceneryEntity {
	@Id
	@Column(name="room_id")
	private int roomId;
	
	@Id
	@Column(name="tile_id")
	private int tileId;
	
	@Id
	@Column(name="scenery_id")
	private int sceneryId;
}
