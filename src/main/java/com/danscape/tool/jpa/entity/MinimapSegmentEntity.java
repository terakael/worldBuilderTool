package com.danscape.tool.jpa.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Lob;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name="minimap_segments")
@IdClass(MinimapSegmentCompositeKey.class)
public class MinimapSegmentEntity {
	@Id private int floor;
	@Id private int segment;
	@Lob private byte[] data;
}
