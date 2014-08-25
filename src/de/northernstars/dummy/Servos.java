package de.northernstars.dummy;

public enum Servos {

	SERVO_1((byte) 0xbb),
	SERVO_2((byte) 0xbd),
	SERVO_3((byte) 0xbe),
	SERVO_4((byte) 0xc0),
	SERVO_5((byte) 0xc3),
	SERVO_6((byte) 0xc5);
	
	public byte dataByte;
	
	private Servos(byte b) {
		dataByte = b;
	}
	
}
