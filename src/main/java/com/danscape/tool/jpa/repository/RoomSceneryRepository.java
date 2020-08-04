package com.danscape.tool.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.danscape.tool.jpa.entity.RoomSceneryCompositeKey;
import com.danscape.tool.jpa.entity.RoomSceneryEntity;

public interface RoomSceneryRepository extends JpaRepository<RoomSceneryEntity, RoomSceneryCompositeKey> {
	@Query(value="select floor, tile_id, scenery_id from room_scenery where floor=:floor and (tile_id%25000 between :x and :x+:w) and (floor(tile_id/25000) between :y and :y+:h)", nativeQuery=true)
	List<RoomSceneryEntity> findAllByXYWH(@Param("floor") int floor, @Param("x") int x, @Param("y") int y, @Param("w") int w,  @Param("h") int h);
	
	public void deleteByFloorAndTileId(int floor, int tileId);
	public List<RoomSceneryEntity> findAllByFloor(int floor);
}
