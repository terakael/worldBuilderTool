package com.danscape.tool.jpa.entity;

import java.io.Serializable;

public class RoomSceneryCompositeKey implements Serializable {
	private static final long serialVersionUID = 4869980235267532865L;
	private int roomId;
	private int tileId;
	private int sceneryId;
}