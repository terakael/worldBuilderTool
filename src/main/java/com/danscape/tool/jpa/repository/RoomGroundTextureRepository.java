package com.danscape.tool.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.danscape.tool.jpa.entity.RoomGroundTextureEntity;

public interface RoomGroundTextureRepository extends JpaRepository<RoomGroundTextureEntity, Integer> {
	public List<RoomGroundTextureEntity> findAllByFloor(int floor);
	
	@Query(value="select floor, tile_id, ground_texture_id from room_ground_textures where floor=:floor and (tile_id%Config.MAP_ROW_TILE_LENGTH between :x and :x+:w) and (floor(tile_id/Config.MAP_ROW_TILE_LENGTH) between :y and :y+:h)", nativeQuery=true)
	public List<RoomGroundTextureEntity> findAllByXYWH(@Param("floor") int floor, @Param("x") int x, @Param("y") int y, @Param("w") int w,  @Param("h") int h);
	
	@Query(value="select distinct floor from room_ground_textures", nativeQuery=true)
	public List<Integer> findDistinctFloors();
}
