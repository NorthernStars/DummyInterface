package de.northernstars.dummy;

public enum Positions {

	STANDBY((byte) 0xca),
	READY((byte) 0xcc);
	
	public byte dataByte;
	
	private Positions(byte b) {
		dataByte = b;
	}
	
}
