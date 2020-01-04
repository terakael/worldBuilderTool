package com.danscape.tool;

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.danscape.tool.jpa.entity.GroundTextureEntity;
import com.danscape.tool.jpa.entity.RoomGroundTextureEntity;
import com.danscape.tool.jpa.entity.RoomSceneryEntity;
import com.danscape.tool.jpa.entity.SceneryEntity;
import com.danscape.tool.jpa.entity.SpriteMapEntity;
import com.danscape.tool.jpa.repository.GroundTextureRepository;
import com.danscape.tool.jpa.repository.RoomGroundTextureRepository;
import com.danscape.tool.jpa.repository.RoomSceneryRepository;
import com.danscape.tool.jpa.repository.SceneryRepository;
import com.danscape.tool.jpa.repository.SpriteMapRepository;

import lombok.Setter;

public class Swing extends Frame implements ActionListener {
	private static final long serialVersionUID = 7554091224387792355L;
	
	@Autowired
	private SpriteMapRepository repository;
	
	@Autowired
	private GroundTextureRepository groundTextureRepository;
	
	@Autowired
	private RoomGroundTextureRepository roomGroundTextureRepository;
	
	@Autowired
	private RoomSceneryRepository roomSceneryRepository;
	
	@Autowired
	private SceneryRepository sceneryRepository;
	
	private static final int windowWidth = 800;
	private static final int windowHeight = 600;
	private static boolean fullLoad = true;

	private Panel worldPanel = null;
	private Panel guiPanel = null;
	
	private GroundTextureEntity selectedGroundTexture = null;
	private SceneryEntity selectedScenery = null;
	
	private ImageComponent[] worldHolder = new ImageComponent[250*250];
	
	private List<SpriteMapEntity> groundTextureSprites = null; // to show in the ground texture droplist
	private List<SpriteMapEntity> scenerySprites = null; // to show in the scenery droplist
	
	private HashMap<Integer, BufferedImage> imageMap;
	private HashMap<Integer, List<GroundTextureEntity>> groundTextureEntityMapBySpriteMapId;
	private Map<Integer, GroundTextureEntity> groundTextureEntityMapById;
	private List<RoomGroundTextureEntity> roomGroundTextureEntities;
	private List<SceneryEntity> sceneryEntities;
	private Map<Integer, SceneryEntity> sceneryEntitiesById;
	private List<RoomSceneryEntity> roomSceneryEntities;
//	List<RoomGroundTextureEntity> modifiedGroundTextures = new ArrayList<>();
//	List<RoomSceneryEntity> modifiedScenery = new ArrayList<>();
//	List<RoomSceneryEntity> deletedScenery = new ArrayList<>();
	private boolean deleteScenery = false;
	
	public Swing() {
		
	}
	
	public void go() throws IOException, SQLException {		
		
		roomGroundTextureEntities = roomGroundTextureRepository.findAll().stream().sorted(Comparator.comparing(RoomGroundTextureEntity::getTileId)).collect(Collectors.toList());
		groundTextureEntityMapBySpriteMapId = new HashMap<>();
		groundTextureEntityMapById = groundTextureRepository.findAll().stream().collect(Collectors.toMap(GroundTextureEntity::getId, Function.identity()));
		imageMap = new HashMap<>();
		
		groundTextureSprites = repository.getGroundTextures();
		for (SpriteMapEntity spriteMap : groundTextureSprites) {
			if (!imageMap.containsKey(spriteMap.getId()))
				imageMap.put(spriteMap.getId(), ImageIO.read(spriteMap.getData().getBinaryStream()));
			
			if (!groundTextureEntityMapBySpriteMapId.containsKey(spriteMap.getId()))
				groundTextureEntityMapBySpriteMapId.put(spriteMap.getId(), new ArrayList<>());
			
			ArrayList<GroundTextureEntity> groundTextures = new ArrayList<>(groundTextureRepository.findAllBySpriteMapId(spriteMap.getId()));
			groundTextureEntityMapBySpriteMapId.get(spriteMap.getId()).addAll(groundTextures);
		}
		
		scenerySprites = repository.getScenerySprites();
		for (SpriteMapEntity spriteMap : scenerySprites) {
			if (!imageMap.containsKey(spriteMap.getId()))
				imageMap.put(spriteMap.getId(), ImageIO.read(spriteMap.getData().getBinaryStream()));
		}
		
		sceneryEntities = sceneryRepository.findAll();
		sceneryEntitiesById = sceneryEntities.stream().collect(Collectors.toMap(SceneryEntity::getId, Function.identity()));
		roomSceneryEntities = roomSceneryRepository.findAll();
		
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		
		setBackground(new Color(0));
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		c.weightx = 0.4;
		c.weighty = 0.5;
		worldPanel = setupWorldPanel();
		
		System.out.println("creating scroll pane");
		ScrollPane scroll = new ScrollPane();
		scroll.add(worldPanel);
		
		System.out.println("adding scroll pane");
		add(scroll, c);
		System.out.println("added scroll pane");
		c.gridwidth = 1;
		c.gridx = 2;
		c.weightx = 0.0;
		c.weighty = 0;
		
		System.out.println("setting up gui panel");
		guiPanel = setupGuiPanel();
		
		System.out.println("adding gui panel");
		add(guiPanel, c);
		System.out.println("added gui panel");
		
		setTitle("tool");
		setSize(windowWidth, windowHeight);
		setVisible(true);
		
		System.out.println("finished loading everything");
	}
	
	class ImageComponent extends Component {
		private static final long serialVersionUID = 1684094236921916561L;
		@Setter private GroundTextureEntity groundTexture = null;
		@Setter private SceneryEntity sceneryEntity = null;
		
		public ImageComponent(GroundTextureEntity groundTexture) {
			this.groundTexture = groundTexture;
		}
		
		@Override
		public void paint(Graphics g) {
			if (groundTexture != null && imageMap.containsKey(groundTexture.getSpriteMapId())) {
				g.drawImage(imageMap.get(groundTexture.getSpriteMapId()), 0, 0, 32, 32, groundTexture.getX(), groundTexture.getY(), groundTexture.getX() + 32, groundTexture.getY() + 32, null);
			}
			
			if (sceneryEntity != null && imageMap.containsKey(sceneryEntity.getSpriteMapId())) {
				g.drawImage(imageMap.get(sceneryEntity.getSpriteMapId()), 
						0, 0, 
						32, 32, 
						sceneryEntity.getX(), sceneryEntity.getY(), 
						sceneryEntity.getX() + sceneryEntity.getW(), sceneryEntity.getY() + sceneryEntity.getH(), null);
			}
		}
		
		@Override
		public Dimension getPreferredSize() {
			return new Dimension(32, 32);
		}
		
		@Override
        public Dimension getMinimumSize() {
			return new Dimension(32, 32);
        }

        @Override
        public Dimension getMaximumSize() {
        	return new Dimension(32, 32);
        }
	}
	
	private Panel setupWorldPanel() {
		Panel panel = new Panel();
		panel.setBackground(new Color(80, 80, 80));
		
		if (fullLoad) {
			panel.setLayout(new GridLayout(250, 250));
			System.out.println("loading world holder...");
			for (RoomGroundTextureEntity entity : roomGroundTextureEntities) {
				worldHolder[entity.getTileId()] = new ImageComponent(groundTextureEntityMapById.get(entity.getGroundTextureId()));
				panel.add(worldHolder[entity.getTileId()]);
			}
			
			for (RoomSceneryEntity entity : roomSceneryEntities) {
				worldHolder[entity.getTileId()].sceneryEntity = sceneryEntitiesById.get(entity.getSceneryId());
			}
			
			System.out.println("world holder loaded.");
		}
		
		panel.addMouseListener(new MouseAdapter() {
	         @Override
	         public void mousePressed(MouseEvent e) {
	        	 Component component = panel.getComponentAt(e.getPoint());
	        	 if (component == null || !(component instanceof ImageComponent))
	        		 return;
	        	 
	        	 ImageComponent imgComponent = (ImageComponent)component;
	        	 
	        	 int tileId = ArrayUtils.indexOf(worldHolder, imgComponent);
	        	 if (selectedGroundTexture != null) {
//	        		 modifiedGroundTextures.add(new RoomGroundTextureEntity(1, tileId, selectedGroundTexture.getId()));
	        		 roomGroundTextureRepository.save(new RoomGroundTextureEntity(1, tileId, selectedGroundTexture.getId()));
	        		 imgComponent.groundTexture = selectedGroundTexture;
	        	 }
	        	 
	        	 if (deleteScenery == true) {
	        		 if (imgComponent.sceneryEntity != null) {
	        			 roomSceneryRepository.delete(new RoomSceneryEntity(1, tileId, imgComponent.sceneryEntity.getId()));
//	        			 roomSceneryRepository.deleteByRoomIdAndTileId(1, tileId);
	        		 }
	        		 
	        		 imgComponent.sceneryEntity = null;
	        	 }
	        	 else if (selectedScenery != null) {
//	        		 modifiedScenery.add(new RoomSceneryEntity(1, tileId, selectedScenery.getId()));
	        		 roomSceneryRepository.save(new RoomSceneryEntity(1, tileId, selectedScenery.getId()));
	        		 imgComponent.sceneryEntity = selectedScenery;
	        	 }
        		 
	        	 panel.revalidate();
	        	 component.repaint();
	         }
		});
		
		return panel;
	}
	
	private Panel setupGuiPanel() {
		Panel panel = new Panel();
		panel.setLayout(null);
		panel.setSize(200, 500);
		panel.setBackground(new Color(0, 100, 0));
		
		Panel selectedSpritePanel = new Panel();
		GridLayout selectedSpriteLayout = new GridLayout();
		selectedSpritePanel.setLayout(selectedSpriteLayout);
		selectedSpritePanel.setBounds(0, 200, 200, 300);
		selectedSpritePanel.setBackground(new Color(50, 50, 50));
		selectedSpritePanel.addMouseListener(new MouseAdapter() {
	         @Override
	         public void mousePressed(MouseEvent e) {
	        	 Component component = selectedSpritePanel.getComponentAt(e.getPoint());
	        	 if (component == null || !(component instanceof ImageComponent))
	        		 return;
	        	 
	        	 selectedGroundTexture = ((ImageComponent)component).groundTexture;
	         }
		});
		panel.add(selectedSpritePanel);
		
		Checkbox deleteCheckbox = new Checkbox();
		deleteCheckbox.setBounds(10, 20, 20, 20);
		deleteCheckbox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				deleteScenery = e.getStateChange() == 1;
			}
			
		});
		panel.add(deleteCheckbox);
		
		Choice groundTextureChoice = new Choice();
		groundTextureChoice.setBounds(0, 150, 200, 100);
		groundTextureChoice.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				for (Component c : selectedSpritePanel.getComponents()) {
					if (c instanceof ImageComponent) {
						selectedSpritePanel.remove(c);
					}
				}
				
				SpriteMapEntity selectedEntity = null;
				for (SpriteMapEntity entity : groundTextureSprites) {
					if (entity.getName().equals(e.getItem())) {
						selectedEntity = entity;
						break;
					}
				}
				
				if (selectedEntity != null) {
					if (groundTextureEntityMapBySpriteMapId.containsKey(selectedEntity.getId())) {
						int maxx = 0;
						int maxy = 0;
						for (GroundTextureEntity groundTexture : groundTextureEntityMapBySpriteMapId.get(selectedEntity.getId())) {
							maxx = Math.max(maxx, groundTexture.getX());
							maxy = Math.max(maxy, groundTexture.getY());
							selectedSpritePanel.add(new ImageComponent(groundTexture));	
						}
						
						selectedSpriteLayout.setColumns((maxx/32) + 1);
						selectedSpriteLayout.setRows((maxy/32) + 1);
//						selectedSpritePanel.setSize(new Dimension(maxx, maxy));
					}
				}

				selectedSpritePanel.revalidate();
				selectedSpritePanel.repaint();
			}
			
		});
		groundTextureChoice.add("");
		for (SpriteMapEntity entity : groundTextureSprites) {
			groundTextureChoice.add(entity.getName());
		}
		panel.add(groundTextureChoice);
		
		
		Choice sceneryChoice = new Choice();
		sceneryChoice.setBounds(0, 100, 200, 100);
		sceneryChoice.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				selectedScenery = null;
				String item = e.getItem().toString();

				if (!item.isEmpty()) {
					try {
						int id = Integer.valueOf(item.substring(0, item.indexOf(':')));
					
						for (SceneryEntity entity : sceneryEntities) {
							if (entity.getId() == id) {
								selectedScenery = entity;
								return;
							}
						}
					} catch (NumberFormatException ex) {
						// idgaf
					}
				}
		}});
		sceneryChoice.add("");
		for (SceneryEntity entity : sceneryEntities) {
			sceneryChoice.add(entity.getId() + ": " + entity.getName());
		}
		panel.add(sceneryChoice);
		
		return panel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
	}
}
