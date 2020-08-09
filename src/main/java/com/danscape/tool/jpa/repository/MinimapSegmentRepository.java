package com.danscape.tool.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.danscape.tool.jpa.entity.MinimapSegmentCompositeKey;
import com.danscape.tool.jpa.entity.MinimapSegmentEntity;

@Repository
public interface MinimapSegmentRepository extends JpaRepository<MinimapSegmentEntity, MinimapSegmentCompositeKey> {

}
