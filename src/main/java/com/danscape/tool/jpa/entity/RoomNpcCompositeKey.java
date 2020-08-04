package com.danscape.tool.jpa.entity;

import java.io.Serializable;

public class RoomNpcCompositeKey implements Serializable {
	private static final long serialVersionUID = 8235899953737919732L;
	private int floor;
	private int tileId;
	private int npcId;
}
