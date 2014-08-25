package de.northernstars.dummy;

public enum Commands {

	OPEN_HAND((byte) 0xc6),
	CLOSE_HAND((byte) 0xc9),
	
	GET_SERVO_SPEED((byte) 0xfa),
	SET_SERVO_SPEED((byte) 0xfc);
	
	public byte dataByte;
	
	private Commands(byte b) {
		dataByte = b;
	}
	
}
