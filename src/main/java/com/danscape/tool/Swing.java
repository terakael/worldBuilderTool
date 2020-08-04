package com.danscape.tool;

import java.awt.Checkbox;
import java.awt.CheckboxGroup;
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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.danscape.tool.jpa.entity.GroundTextureEntity;
import com.danscape.tool.jpa.entity.NpcEntity;
import com.danscape.tool.jpa.entity.PlayerEntity;
import com.danscape.tool.jpa.entity.RoomGroundTextureEntity;
import com.danscape.tool.jpa.entity.RoomNpcEntity;
import com.danscape.tool.jpa.entity.RoomSceneryEntity;
import com.danscape.tool.jpa.entity.SceneryEntity;
import com.danscape.tool.jpa.entity.SpriteFrameEntity;
import com.danscape.tool.jpa.entity.SpriteMapEntity;
import com.danscape.tool.jpa.repository.GroundTextureRepository;
import com.danscape.tool.jpa.repository.NpcRepository;
import com.danscape.tool.jpa.repository.PlayerRepository;
import com.danscape.tool.jpa.repository.RoomGroundTextureRepository;
import com.danscape.tool.jpa.repository.RoomNpcRepository;
import com.danscape.tool.jpa.repository.RoomSceneryRepository;
import com.danscape.tool.jpa.repository.SceneryRepository;
import com.danscape.tool.jpa.repository.SpriteFrameRepository;
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
	
	@Autowired
	private SpriteFrameRepository spriteFrameRepository;
	
	@Autowired
	private NpcRepository npcRepository;
	
	@Autowired
	private RoomNpcRepository roomNpcRepository;
	
	@Autowired
	private PlayerRepository playerRepository;
	
	private static int floor = -1;
	private static int localWidth = 50;
	private static int localHeight = 30;
	
	private static final int windowWidth = (32*localWidth) + 245;
	private static final int windowHeight = 32*localHeight + 50;
	
	private static boolean fullLoad = true;

	private Panel worldPanel = null;
	private Panel guiPanel = null;
	
	private GroundTextureEntity selectedGroundTexture = null;
	private SceneryEntity selectedScenery = null;
	private NpcEntity selectedNpc = null;
	
	private ImageComponent[] worldHolder = new ImageComponent[localWidth*localHeight];
	private ImageComponent hoverComponent = null;
	int offsetX = 18875;
	int offsetY = 18911;
	
	int counter = 0;
	
	private List<SpriteMapEntity> groundTextureSprites = null; // to show in the ground texture droplist
	private List<SpriteMapEntity> scenerySprites = null; // to show in the scenery droplist
	private List<SpriteMapEntity> npcSprites = null;
	
	private HashMap<Integer, BufferedImage> imageMap;
	private HashMap<Integer, List<GroundTextureEntity>> groundTextureEntityMapBySpriteMapId;
	private Map<Integer, GroundTextureEntity> groundTextureEntityMapById;
	private Map<Integer, SceneryEntity> sceneryEntitiesById;
	private Map<Integer, NpcEntity> npcEntitiesById;
	private Map<Integer, SpriteFrameEntity> spriteFrames;
	private boolean deleteScenery = false;
	
	public Swing() {
		
	}
	
	public void go() throws IOException, SQLException {
		Optional<PlayerEntity> godEntity = playerRepository.findById(3);// god
		if (godEntity.isPresent()) {
			floor = godEntity.get().getFloor();
			offsetX = (godEntity.get().getTileId() % 25000) - (localWidth / 2);
			offsetY = (godEntity.get().getTileId() / 25000) - (localHeight / 2);
		}
		
		spriteFrames = spriteFrameRepository.findAll().stream().collect(Collectors.toMap(SpriteFrameEntity::getId, Function.identity()));
		
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
		sceneryEntitiesById = sceneryRepository.findAll().stream().collect(Collectors.toMap(SceneryEntity::getId, Function.identity()));
		
		npcSprites = repository.getNpcSprites();
		for (SpriteMapEntity spriteMap : npcSprites) {
			if (!imageMap.containsKey(spriteMap.getId()))
				imageMap.put(spriteMap.getId(), ImageIO.read(spriteMap.getData().getBinaryStream()));
		}
		npcEntitiesById = npcRepository.findAll().stream().collect(Collectors.toMap(NpcEntity::getId, Function.identity()));
		
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
		@Setter private NpcEntity npcEntity = null;
		
		public ImageComponent() {
			
		}
		
		public ImageComponent(GroundTextureEntity groundTexture, SceneryEntity sceneryEntity, NpcEntity npcEntity) {
			this.groundTexture = groundTexture;
			this.sceneryEntity = sceneryEntity;
			this.npcEntity = npcEntity;
		}
		
		@Override
		public void paint(Graphics g) {
			if (groundTexture != null && imageMap.containsKey(groundTexture.getSpriteMapId())) {
				g.drawImage(imageMap.get(groundTexture.getSpriteMapId()), 0, 0, 32, 32, groundTexture.getX(), groundTexture.getY(), groundTexture.getX() + 32, groundTexture.getY() + 32, null);
			}
			
			if (sceneryEntity != null) {
				SpriteFrameEntity spriteFrame = spriteFrames.get(sceneryEntity.getSpriteFrameId());
				if (imageMap.containsKey(spriteFrame.getSpriteMapId())) {
					g.drawImage(imageMap.get(spriteFrame.getSpriteMapId()), 
							0, 0, 
							32, 32, 
							spriteFrame.getX(), spriteFrame.getY(), 
							spriteFrame.getX() + spriteFrame.getW(), spriteFrame.getY() + spriteFrame.getH(), null);
				}
			}
			
			if (npcEntity != null) {
				SpriteFrameEntity spriteFrame = spriteFrames.get(npcEntity.getDownId());
				if (imageMap.containsKey(spriteFrame.getSpriteMapId())) {
					g.drawImage(imageMap.get(spriteFrame.getSpriteMapId()), 
							0, 0, 
							32, 32, 
							spriteFrame.getX(), spriteFrame.getY(), 
							spriteFrame.getX() + spriteFrame.getW(), spriteFrame.getY() + spriteFrame.getH(), null);
				}
			}
			
			if (hoverComponent == this) {
				if (selectedGroundTexture != null && imageMap.containsKey(selectedGroundTexture.getSpriteMapId())) {
					g.drawImage(imageMap.get(selectedGroundTexture.getSpriteMapId()), 0, 0, 32, 32, selectedGroundTexture.getX(), selectedGroundTexture.getY(), selectedGroundTexture.getX() + 32, selectedGroundTexture.getY() + 32, null);
				}
				
				if (selectedScenery != null) {
					SpriteFrameEntity spriteFrame = spriteFrames.get(selectedScenery.getSpriteFrameId());
					if (imageMap.containsKey(spriteFrame.getSpriteMapId())) {
						g.drawImage(imageMap.get(spriteFrame.getSpriteMapId()), 
								0, 0, 
								32, 32, 
								spriteFrame.getX(), spriteFrame.getY(), 
								spriteFrame.getX() + spriteFrame.getW(), spriteFrame.getY() + spriteFrame.getH(), null);
					}
				}
				
				if (selectedNpc != null) {
					SpriteFrameEntity spriteFrame = spriteFrames.get(selectedNpc.getDownId());
					if (imageMap.containsKey(spriteFrame.getSpriteMapId())) {
						g.drawImage(imageMap.get(spriteFrame.getSpriteMapId()), 
								0, 0, 
								32, 32, 
								spriteFrame.getX(), spriteFrame.getY(), 
								spriteFrame.getX() + spriteFrame.getW(), spriteFrame.getY() + spriteFrame.getH(), null);
					}
				}
				
				g.setColor(Color.WHITE);				
				g.drawRect(0, 0, 2, 2);
				g.drawRect(29, 0, 2, 2);
				g.drawRect(0, 29, 2, 2);
				g.drawRect(30, 29, 2, 2);
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
		panel.setFocusable(true);
		
		
		if (fullLoad) {
			panel.setLayout(new GridLayout(localHeight, localWidth));
			System.out.println("loading world holder...");
			
			for (int i = 0; i < localWidth * localHeight; ++i) {
				worldHolder[i] = new ImageComponent();
				panel.add(worldHolder[i]);
			}

			updateWorldHolder();
			System.out.println("world holder loaded.");
		}
		
		panel.addMouseListener(new MouseAdapter() {
	         @Override
	         public void mousePressed(MouseEvent e) {
	        	 panel.requestFocus();
	        	 
	        	 if (SwingUtilities.isRightMouseButton(e))
	        		 return;
	        	 
	        	 Component component = panel.getComponentAt(e.getPoint());
	        	 if (component == null || !(component instanceof ImageComponent))
	        		 return;
	        	 
	        	 ImageComponent imgComponent = (ImageComponent)component;
	        	 
	        	 int localTileId = ArrayUtils.indexOf(worldHolder, imgComponent);
	        	 int worldTileX = (localTileId % localWidth) + offsetX;
	        	 int worldTileY = (localTileId / localWidth) + offsetY;
	        	 int tileId = (worldTileY * 25000) + worldTileX;
	        	 
	        	 if (selectedGroundTexture != null) {
	        		 roomGroundTextureRepository.save(new RoomGroundTextureEntity(floor, tileId, selectedGroundTexture.getId()));
	        		 imgComponent.groundTexture = selectedGroundTexture;
	        	 }
	        	 
	        	 if (deleteScenery == true) {
	        		 if (imgComponent.sceneryEntity != null) {
	        			 roomSceneryRepository.delete(new RoomSceneryEntity(floor, tileId, imgComponent.sceneryEntity.getId()));
	        		 }
	        		 imgComponent.sceneryEntity = null;
	        		 
	        		 if (imgComponent.npcEntity != null) {
	        			 roomNpcRepository.delete(new RoomNpcEntity(floor, tileId, imgComponent.npcEntity.getId()));
	        		 }
	        		 imgComponent.npcEntity = null;
	        	 }
	        	 else {
	        		 if (selectedScenery != null) {
		        		 roomSceneryRepository.save(new RoomSceneryEntity(floor, tileId, selectedScenery.getId()));
		        		 imgComponent.sceneryEntity = selectedScenery;
		        	 }
	        		 if (selectedNpc != null) {
	        			 roomNpcRepository.save(new RoomNpcEntity(floor, tileId, selectedNpc.getId()));
	        			 imgComponent.npcEntity = selectedNpc;
	        		 }
		         }
        		 
	        	 panel.revalidate();
	        	 component.repaint();
	         }
		});
		
		panel.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
	         public void mouseMoved(MouseEvent e) {	        	 
	        	 Component component = panel.getComponentAt(e.getPoint());
	        	 if (component == null || !(component instanceof ImageComponent))
	        		 return;
	        	 
	        	 if (component == hoverComponent)
	        		 return;
	        	 
	        	 if (hoverComponent != null)
	        		 hoverComponent.repaint();
	        	 
	        	 hoverComponent = (ImageComponent)component;
	        	 component.repaint();
	         }
		});
		
		panel.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
//				panel.requestFocus();
				switch (e.getKeyCode()) {
				case 37: // left
					offsetX -= localWidth/ 2;
					updateWorldHolder();
					break;
				case 38: // up
					offsetY -= localHeight / 2;
					updateWorldHolder();
					break;
				case 39: // right
					offsetX += localWidth / 2;
					updateWorldHolder();
					break;
				case 40: // down
					offsetY += localHeight / 2;
					updateWorldHolder();
					break;
					
				case 33: // pageup
					++floor;
					updateWorldHolder();
					break;
					
				case 34: // pagedown
					--floor;
					updateWorldHolder();
					break;
				
				case 27:
					selectedGroundTexture = null;
	        		selectedScenery = null;
					break;
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
			}
			
		});
		
		return panel;
	}
	
	private void updateWorldHolder() {
		System.out.println(String.format("offsetX=%d, offsetY=%d", offsetX, offsetY));
		Map<Integer, RoomGroundTextureEntity> groundTextures = roomGroundTextureRepository.findAllByXYWH(floor, offsetX, offsetY, localWidth, localHeight).stream().collect(Collectors.toMap(RoomGroundTextureEntity::getTileId, Function.identity()));
		Map<Integer, RoomSceneryEntity> scenery = roomSceneryRepository.findAllByXYWH(floor, offsetX, offsetY, localWidth, localHeight).stream().collect(Collectors.toMap(RoomSceneryEntity::getTileId, Function.identity()));
		Map<Integer, RoomNpcEntity> npcs = roomNpcRepository.findAllByXYWH(floor, offsetX, offsetY, localWidth, localHeight).stream().collect(Collectors.toMap(RoomNpcEntity::getTileId, Function.identity()));
		
		for (int y = 0; y < localHeight; ++y) {
			for (int x = 0; x < localWidth; ++x) {
				int tileX = x + offsetX;
				int tileY = y + offsetY;
				
				int tileId = (tileY * 25000) + tileX;
				RoomGroundTextureEntity entity = groundTextures.get(tileId);
				worldHolder[(y*localWidth) + x].groundTexture = groundTextureEntityMapById.get(entity == null ? 0 : entity.getGroundTextureId());
				
				RoomSceneryEntity roomSceneryEntity = scenery.get(tileId);
				worldHolder[(y*localWidth) + x].sceneryEntity = roomSceneryEntity == null ? null : sceneryEntitiesById.get(roomSceneryEntity.getSceneryId());
				
				RoomNpcEntity roomNpcEntity = npcs.get(tileId);
				worldHolder[(y*localWidth) + x].npcEntity = roomNpcEntity == null ? null : npcEntitiesById.get(roomNpcEntity.getNpcId());
				
				worldHolder[(y*localWidth) + x].repaint();
			}
		}
	}
	
	private Panel setupGuiPanel() {
		Panel panel = new Panel();
		panel.setLayout(null);
		panel.setSize(200, 800);
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
					String entityNameWithId = entity.getId() + ": " + entity.getName();
					if (entityNameWithId.equals(e.getItem())) {
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
							selectedSpritePanel.add(new ImageComponent(groundTexture, null, null));	
						}
						
						selectedSpriteLayout.setColumns((maxx/32) + 1);
						selectedSpriteLayout.setRows((maxy/32) + 1);
					}
				}

				selectedSpritePanel.revalidate();
				selectedSpritePanel.repaint();
			}
			
		});
		groundTextureChoice.add("");
		for (SpriteMapEntity entity : groundTextureSprites) {
			groundTextureChoice.add(entity.getId() + ": " + entity.getName());
		}
		panel.add(groundTextureChoice);
		
		
		Choice sceneryChoice = new Choice();
		sceneryChoice.setBounds(0, 150, 200, 100);
		sceneryChoice.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				selectedScenery = null;
				String item = e.getItem().toString();

				if (!item.isEmpty()) {
					try {
						int id = Integer.valueOf(item.substring(0, item.indexOf(':')));
						selectedScenery = sceneryEntitiesById.get(id);
					} catch (NumberFormatException ex) {
						// idgaf
					}
				}
		}});
		sceneryChoice.add("");
		for (SceneryEntity entity : sceneryEntitiesById.values()) {
			sceneryChoice.add(entity.getId() + ": " + entity.getName());
		}
		sceneryChoice.setVisible(true);
		panel.add(sceneryChoice);
		
		Choice npcChoice = new Choice();
		npcChoice.setBounds(0, 150, 200, 100);
		npcChoice.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				selectedNpc = null;
				String item = e.getItem().toString();

				if (!item.isEmpty()) {
					try {
						int id = Integer.valueOf(item.substring(0, item.indexOf(':')));
						selectedNpc = npcEntitiesById.get(id);
					} catch (NumberFormatException ex) {
						// idgaf
					}
				}
			}
		});
		npcChoice.add("");
		for (NpcEntity entity : npcEntitiesById.values()) {
			npcChoice.add(entity.getId() + ": " + entity.getName());
		}
		npcChoice.setVisible(false);
		panel.add(npcChoice);
		
		
		int y = 0;
		CheckboxGroup cbg = new CheckboxGroup();
		String[] names = new String[] {"GroundItems", "Scenery", "NPCs"};
		for (String name : names) {
			Checkbox cb = new Checkbox(name, cbg, y == 0);
			cb.setBounds(0, 80 + y, 100, 20);
			cb.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent e) {
					groundTextureChoice.setVisible(false);
					selectedSpritePanel.setVisible(false);
					sceneryChoice.setVisible(false);
					npcChoice.setVisible(false);
					
					selectedScenery = null;
					selectedGroundTexture = null;
					selectedNpc = null;
					
					switch (name) {
					case "GroundItems": {
						groundTextureChoice.setVisible(true);
						selectedSpritePanel.setVisible(true);
						break;
					}
					
					case "Scenery": {
						sceneryChoice.setVisible(true);
						break;
					}
					
					case "NPCs": {
						npcChoice.setVisible(true);
						break;
					}
					}
				}
				
			});
			
			panel.add(cb);
			y += 20;
		}
		
		
		return panel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
	}
}
