package com.danscape.tool;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.danscape.tool.jpa.entity.GroundTextureEntity;
import com.danscape.tool.jpa.entity.MinimapSegmentEntity;
import com.danscape.tool.jpa.entity.RoomGroundTextureEntity;
import com.danscape.tool.jpa.entity.RoomSceneryEntity;
import com.danscape.tool.jpa.entity.SceneryEntity;
import com.danscape.tool.jpa.entity.SpriteMapEntity;
import com.danscape.tool.jpa.repository.GroundTextureRepository;
import com.danscape.tool.jpa.repository.MinimapSegmentRepository;
import com.danscape.tool.jpa.repository.RoomGroundTextureRepository;
import com.danscape.tool.jpa.repository.RoomSceneryRepository;
import com.danscape.tool.jpa.repository.SceneryRepository;
import com.danscape.tool.jpa.repository.SpriteMapRepository;

@Component
public class MinimapGenerator {
	@Autowired
	private RoomGroundTextureRepository roomGroundTextureRepository;
	
	@Autowired
	private GroundTextureRepository groundTextureRepository;
	
	@Autowired
	private SpriteMapRepository spriteMapRepository;
	
	@Autowired
	private MinimapSegmentRepository minimapSegmentRepository;
	
	@Autowired
	private SceneryRepository sceneryRepository;
	
	@Autowired 
	RoomSceneryRepository roomSceneryRepository;
	
	public void generate(Integer floor, Integer segment) throws IOException, SQLException {
		List<Integer> floors = new ArrayList<>();
		if (floor == null) {
			// all floors, all segments
			floors = roomGroundTextureRepository.findDistinctFloors();
		} else if (segment != null) {
			// specific floor, specific segment
//			roomGroundTextures = roomGroundTextureRepository.findAllByXYWH(floor, x, y, w, h)
			return;// TODO
		} else {
			// specific floor, all segments
			floors.add(floor);
		}
		
		for (int currentFloor : floors)
			generateForFloor(currentFloor);
	}
	
	private Map<Integer, Integer[]> loadTextureAverageColors(List<RoomGroundTextureEntity> roomGroundTextures) {
		Set<Integer> distictGroundTextureIds = roomGroundTextures.stream().map(e -> e.getGroundTextureId()).collect(Collectors.toSet());
		Set<GroundTextureEntity> distinctGroundTextures = groundTextureRepository.findAll().stream()
				.filter(e -> distictGroundTextureIds.contains(e.getId()))
				.collect(Collectors.toSet());
		
		Map<Integer, BufferedImage> spriteMapsById = spriteMapRepository.getGroundTextures().stream().collect(Collectors.toMap(SpriteMapEntity::getId, e -> {
			try {
				return ImageIO.read(e.getData().getBinaryStream());
			} catch (IOException | SQLException e1) {
			}
			return null;
		}));
		
		Map<Integer, Integer[]> groundTextureIdColors = new HashMap<>();
		for (GroundTextureEntity entity : distinctGroundTextures) {
			if (!spriteMapsById.containsKey(entity.getSpriteMapId()))
				continue;
			
			BufferedImage groundTextureImage = spriteMapsById.get(entity.getSpriteMapId()).getSubimage(entity.getX(), entity.getY(), 32, 32);
			
			Integer[] averages = {
					getAverageColorForSection(groundTextureImage, 0, 0, 16),
					getAverageColorForSection(groundTextureImage, 16, 0, 16),
					getAverageColorForSection(groundTextureImage, 0, 16, 16),
					getAverageColorForSection(groundTextureImage, 16, 16, 16)
			};
			
			groundTextureIdColors.put(entity.getId(), averages);
		}
		
		return groundTextureIdColors;
	}
	
	private int getAverageColorForSection(BufferedImage image, int xPos, int yPos, int len) {
		int cumulativeRed = 0;
		int cumulativeGreen = 0;
		int cumulativeBlue = 0;
		for (int y = yPos; y < yPos + len; ++y) {
			for (int x = xPos; x < xPos + len; ++x) {
				Color rgb = new Color(image.getRGB(x, y));
				cumulativeRed += rgb.getRed();
				cumulativeGreen += rgb.getGreen();
				cumulativeBlue += rgb.getBlue();
			}
		}
		Color average = new Color(cumulativeRed / (len * len), cumulativeGreen / (len * len), cumulativeBlue / (len * len));
		return average.getRGB();
	}
	
	private void generateForFloor(int floor) throws IOException {
		List<RoomGroundTextureEntity> roomGroundTextures = roomGroundTextureRepository.findAllByFloor(floor);
		Map<Integer, Integer[]> groundTextureIdColors = loadTextureAverageColors(roomGroundTextures);
		
		Map<Integer, Integer> wallSceneryIdsImpassableIds = sceneryRepository.findAll().stream()
				.filter(e -> e.getImpassable() != 0 && e.getImpassable() != 15)
				.collect(Collectors.toMap(SceneryEntity::getId, SceneryEntity::getImpassable));
		
		Map<Integer, Integer> wallSidesByTileId = roomSceneryRepository.findAllByFloor(floor).stream()
				.filter(e -> wallSceneryIdsImpassableIds.containsKey(e.getSceneryId()))
				.collect(Collectors.toMap(RoomSceneryEntity::getTileId, e -> wallSceneryIdsImpassableIds.get(e.getSceneryId())));
		
		
//		Map<Integer, SceneryEntity> wallsByTileId = roomSceneryRepository.findAll().stream().filter(e -> e.)
		
		
		while (!roomGroundTextures.isEmpty()) {
			List<RoomGroundTextureEntity> currentSegment = new ArrayList<>();
			int tileX = roomGroundTextures.get(0).getTileId() % 25000;
			int tileY = roomGroundTextures.get(0).getTileId() / 25000;
			int segmentId = ((tileY / 25) * 1000) + (tileX / 25);
			currentSegment.addAll(roomGroundTextures.stream().filter(e -> {
				final int x = e.getTileId() % 25000;
				final int y = e.getTileId() / 25000;
				return segmentId == ((y / 25) * 1000) + (x / 25);
			}).collect(Collectors.toList()));
			
			BufferedImage outImage = new BufferedImage(50, 50, BufferedImage.TYPE_INT_RGB);
			for (RoomGroundTextureEntity texture : currentSegment) {
				// coords between 0-25
				int rX = texture.getTileId() % 25;
				int rY = (texture.getTileId() / 25000) % 25;
				
				boolean hasUpperWall = wallSidesByTileId.containsKey(texture.getTileId()) && (wallSidesByTileId.get(texture.getTileId()) & 1) != 0;
				boolean hasLeftWall = wallSidesByTileId.containsKey(texture.getTileId()) && (wallSidesByTileId.get(texture.getTileId()) & 2) != 0;
				boolean hasRightWall = wallSidesByTileId.containsKey(texture.getTileId()) && (wallSidesByTileId.get(texture.getTileId()) & 4) != 0;
				boolean hasBottomWall = wallSidesByTileId.containsKey(texture.getTileId()) && (wallSidesByTileId.get(texture.getTileId()) & 8) != 0;
				
				int grey = Color.DARK_GRAY.getRGB();
				
				outImage.setRGB((rX * 2), (rY * 2), (hasUpperWall || hasLeftWall) ? grey : groundTextureIdColors.get(texture.getGroundTextureId())[0]);
				outImage.setRGB((rX * 2) + 1, (rY * 2), (hasUpperWall || hasRightWall) ? grey : groundTextureIdColors.get(texture.getGroundTextureId())[1]);
				outImage.setRGB((rX * 2), (rY * 2) + 1, (hasLeftWall || hasBottomWall) ? grey : groundTextureIdColors.get(texture.getGroundTextureId())[2]);
				outImage.setRGB((rX * 2) + 1, (rY * 2) + 1, (hasRightWall || hasBottomWall) ? grey : groundTextureIdColors.get(texture.getGroundTextureId())[3]);
			}
			
			// sometimes a wall is not on a texture (e.g. cave wall bottoms sit on black unwalkable)
			// we want to draw these too.
			List<Integer> remainingWallsByTileId = wallSidesByTileId.keySet().stream().filter(e -> {
				final int x = e % 25000;
				final int y = e / 25000;
				return segmentId == ((y / 25) * 1000) + (x / 25) && !currentSegment.stream().map(RoomGroundTextureEntity::getTileId).collect(Collectors.toSet()).contains(e);
			}).collect(Collectors.toList());
			
			for (Integer tileId : remainingWallsByTileId) {
				int rX = tileId % 25;
				int rY = (tileId / 25000) % 25;
				
				boolean hasUpperWall = wallSidesByTileId.containsKey(tileId) && (wallSidesByTileId.get(tileId) & 1) != 0;
				boolean hasLeftWall = wallSidesByTileId.containsKey(tileId) && (wallSidesByTileId.get(tileId) & 2) != 0;
				boolean hasRightWall = wallSidesByTileId.containsKey(tileId) && (wallSidesByTileId.get(tileId) & 4) != 0;
				boolean hasBottomWall = wallSidesByTileId.containsKey(tileId) && (wallSidesByTileId.get(tileId) & 8) != 0;
				
				int grey = Color.DARK_GRAY.getRGB();
				
				outImage.setRGB((rX * 2), (rY * 2), (hasUpperWall || hasLeftWall) ? grey : Color.BLACK.getRGB());
				outImage.setRGB((rX * 2) + 1, (rY * 2), (hasUpperWall || hasRightWall) ? grey : Color.BLACK.getRGB());
				outImage.setRGB((rX * 2), (rY * 2) + 1, (hasLeftWall || hasBottomWall) ? grey : Color.BLACK.getRGB());
				outImage.setRGB((rX * 2) + 1, (rY * 2) + 1, (hasRightWall || hasBottomWall) ? grey : Color.BLACK.getRGB());
			}
			
//			ImageIO.write(outImage, "png", new File("C:/work/test.png"));
			
			ByteArrayOutputStream bas = new ByteArrayOutputStream();
			ImageIO.write(outImage, "png", bas);
			byte[] bytes = bas.toByteArray();
			
//			DataBufferByte data = (DataBufferByte)outImage.getRaster().getDataBuffer();
			minimapSegmentRepository.save(new MinimapSegmentEntity(floor, segmentId, bytes));
			
			roomGroundTextures.removeAll(currentSegment);
		}
	}
}
