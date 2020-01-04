package com.danscape.tool.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.danscape.tool.jpa.entity.GroundTextureEntity;
import com.danscape.tool.jpa.entity.RoomGroundTextureEntity;

public interface GroundTextureRepository extends JpaRepository<GroundTextureEntity, Integer> {
	List<GroundTextureEntity> findAllBySpriteMapId(int spriteMapId);
}
