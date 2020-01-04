package com.danscape.tool.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.danscape.tool.jpa.entity.RoomSceneryCompositeKey;
import com.danscape.tool.jpa.entity.RoomSceneryEntity;

public interface RoomSceneryRepository extends JpaRepository<RoomSceneryEntity, RoomSceneryCompositeKey> {
	public void deleteByRoomIdAndTileId(int roomId, int tileId);
}
