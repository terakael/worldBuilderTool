package com.danscape.tool.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.danscape.tool.jpa.entity.RoomNpcCompositeKey;
import com.danscape.tool.jpa.entity.RoomNpcEntity;

public interface RoomNpcRepository extends JpaRepository<RoomNpcEntity, RoomNpcCompositeKey> {
	@Query(value="select floor, tile_id, npc_id from room_npcs where floor=:floor and (tile_id%Config.MAP_ROW_TILE_LENGTH between :x and :x+:w) and (floor(tile_id/Config.MAP_ROW_TILE_LENGTH) between :y and :y+:h)", nativeQuery=true)
	List<RoomNpcEntity> findAllByXYWH(@Param("floor") int floor, @Param("x") int x, @Param("y") int y, @Param("w") int w,  @Param("h") int h);
}
