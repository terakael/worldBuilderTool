package com.danscape.tool.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.danscape.tool.jpa.entity.SpriteMapEntity;

public interface SpriteMapRepository extends JpaRepository<SpriteMapEntity, Integer> {
	@Query(value="select distinct sprite_map_id as id, name, data from ground_textures " + 
			"inner join sprite_maps on sprite_maps.id = ground_textures.sprite_map_id", nativeQuery=true)
	List<SpriteMapEntity> getGroundTextures();
	
	@Query(value="select distinct sprite_maps.id, sprite_maps.name, sprite_maps.data " + 
			"from scenery " + 
			"inner join sprite_frames on sprite_frames.id = scenery.sprite_frame_id " + 
			"inner join sprite_maps on sprite_maps.id = sprite_frames.sprite_map_id ", nativeQuery=true)
	List<SpriteMapEntity> getScenerySprites();
	
	@Query(value="select distinct sprite_maps.id, sprite_maps.name, sprite_maps.data " + 
			"from npcs " + 
			"inner join sprite_frames on sprite_frames.id = npcs.down_id " + 
			"inner join sprite_maps on sprite_maps.id = sprite_frames.sprite_map_id ", nativeQuery=true)
	List<SpriteMapEntity> getNpcSprites();
}
