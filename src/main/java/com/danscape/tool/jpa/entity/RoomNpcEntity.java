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
@Table(name = "room_npcs")
@IdClass(RoomNpcCompositeKey.class)
public class RoomNpcEntity {
	@Id
	@Column(name="floor")
	private int floor;
	
	@Id
	@Column(name="tile_id")
	private int tileId;
	
	@Column(name="npc_id")
	private int npcId;
}
