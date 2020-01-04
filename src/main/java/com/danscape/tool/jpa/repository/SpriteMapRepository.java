package com.danscape.tool.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.danscape.tool.jpa.entity.SpriteMapEntity;

public interface SpriteMapRepository extends JpaRepository<SpriteMapEntity, Integer> {
	@Query(value="select distinct sprite_map_id as id, name, data from ground_textures " + 
			"inner join sprite_maps on sprite_maps.id = ground_textures.sprite_map_id", nativeQuery=true)
	List<SpriteMapEntity> getGroundTextures();
	
	@Query(value="select distinct sprite_map_id as id, sprite_maps.name, data from scenery " + 
			"inner join sprite_maps on sprite_maps.id = scenery.sprite_map_id", nativeQuery=true)
	List<SpriteMapEntity> getScenerySprites();
}
