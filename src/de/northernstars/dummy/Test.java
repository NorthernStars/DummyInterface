package de.northernstars.dummy;

import java.math.BigInteger;

/**
 * Test class for testing all functionalities
 * @author hannes
 *
 */
public class Test {
	public static void main(String[] args) {
		
		Dummy d = new Dummy();
		if( d.autoConnect() ){
				
			System.out.println("\n---------- STARTING TEST ----------");
			boolean ret;
			
			// POSITIONS
			System.out.print("\nTesting position standby");
			ret = d.setDummyPosition(Positions.STANDBY);
			if( ret ){
				System.out.print("\t[successfull]");
			} else {
				System.err.print("\t[failed]");
			}
			sleep();
			
			System.out.print("\nTesting position ready");
			ret = d.setDummyPosition(Positions.READY);
			if( ret ){
				System.out.print("\t\t[successfull]");
			} else {
				System.err.print("\t\t[failed]");
			}
			sleep();
			
			// SERVO
			System.out.print("\nTesting servo 1");
			ret = d.setServoPosition(Servos.SERVO_1, (byte) 0);
			if( ret ){
				System.out.print("\t\t\t[successfull]");
			} else {
				System.err.print("\t\t\t[failed]");
			}
			sleep();
			
			System.out.print("\nTesting servo 2");
			ret = d.setServoPosition(Servos.SERVO_2, (byte) 0);
			if( ret ){
				System.out.print("\t\t\t[successfull]");
			} else {
				System.err.print("\t\t\t[failed]");
			}
			sleep();
			
			System.out.print("\nTesting servo 3");
			ret = d.setServoPosition(Servos.SERVO_3, (byte) 0);
			if( ret ){
				System.out.print("\t\t\t[successfull]");
			} else {
				System.err.print("\t\t\t[failed]");
			}
			sleep();
			
			System.out.print("\nTesting servo 4");
			ret = d.setServoPosition(Servos.SERVO_4, (byte) 0);
			if( ret ){
				System.out.print("\t\t\t[successfull]");
			} else {
				System.err.print("\t\t\t[failed]");
			}
			sleep();
			
			System.out.print("\nTesting servo 5");
			ret = d.setServoPosition(Servos.SERVO_5, (byte) 0);
			if( ret ){
				System.out.print("\t\t\t[successfull]");
			} else {
				System.err.print("\t\t\t[failed]");
			}
			sleep();
			
			System.out.print("\nTesting servo 6");
			ret = d.setServoPosition(Servos.SERVO_6, (byte) 0);
			if( ret ){
				System.out.print("\t\t\t[successfull]");
			} else {
				System.err.println("\t\t\t[failed]");
			}
			sleep();
			
			
			// HAND
			System.out.print("\nTesting hand");
			ret = d.openHand();
			sleep();
			ret = ret && d.closeHand();
			if( ret ){
				System.out.print("\t\t\t[successfull]");
			} else {
				System.err.print("\t\t\t[failed]");
			}
			sleep();
			
			// SERVO SPEED
			BigInteger vSpeed = new BigInteger( new byte[]{(byte) 0xdd, (byte) 0xcc, (byte) 0xbb, (byte) 0xaa} );
			System.out.print("\nTesting set servo speed");
			ret = d.setServoSpeed( vSpeed );
			if( ret ){
				System.out.print("\t\t[successfull]");
			} else {
				System.err.print("\t\t[failed]");
			}
			sleep();
			
			System.out.print("\nTesting get servo speed");
			BigInteger retUL = d.getServoSpeed();
			if( retUL.compareTo(vSpeed) == 0 ){
				System.out.print("\t\t[successfull]");
			} else {
				System.err.print("\t\t[failed]");
			}
			sleep();
			
		} 
		
		d.disconnect();
	}
	
	/**
	 * Static function to wait 1 seconds
	 */
	public static void sleep(){
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {}
	}

}
