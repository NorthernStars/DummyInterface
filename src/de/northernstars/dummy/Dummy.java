package de.northernstars.dummy;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.hanneseilers.jftdiserial.core.Baudrates;
import de.hanneseilers.jftdiserial.core.DataBits;
import de.hanneseilers.jftdiserial.core.FTDISerial;
import de.hanneseilers.jftdiserial.core.Parity;
import de.hanneseilers.jftdiserial.core.SerialDevice;
import de.hanneseilers.jftdiserial.core.StopBits;
import de.hanneseilers.jftdiserial.core.interfaces.SerialDataRecievedListener;

public class Dummy implements SerialDataRecievedListener {

	private static final byte DUMMY_REQUEST = (byte) 0xb7;
	private static final byte PROTOCOL_START = (byte) 0xb8;
	
	public static final int TIMEOUT = 5000;
	public static final int TIMEOUT_MAX_MULTIPLIER = 3; 
	private static final int CONNECT_MAX_TRIES = 3;
	private static final long CONNECT_TRIES_DELAY = 500;
	
	private FTDISerial serial;
	private LinkedList<Byte> dataBuffer = new LinkedList<Byte>();
	private Logger log = LogManager.getLogger();
	private boolean connected = false;
	
	/**
	 * Default Constructor
	 */
	public Dummy() {
		serial = new FTDISerial(Baudrates.BAUD_115200, DataBits.DATABITS_8, StopBits.STOPBIT_1, Parity.NONE, TIMEOUT);
		serial.addSerialDataRecievedListener(this);
	}
	
	/**
	 * Automatic connect
	 * @return {@code true} if successful, {@code false} otherwise.
	 */
	public boolean autoConnect(){
		return autoSelectDriver() && autoSelectDevice();
	}
	
	/**
	 * Disconnects from Dummy
	 * @return	{@code true} if connected to Dummy, {@code false} otherwise.
	 */
	public boolean disconnect(){
		serial.removeAllSerialDataRecievedListener();
		connected = false;
		return serial.disconnect();
	}
	
	/**
	 * @return {@code true} if connected to Dummy, {@code false} otherwise.
	 */
	public boolean isConnected(){
		return serial.isConnected() && connected;
	}	
	
	/**
	 * Auto connecting to library.
	 * @return	{@code true} if successful, {@code false} otherwise.
	 */
	private boolean autoSelectDriver(){
		for( String libName : serial.getAvailableLibNames() ){
			if( libName.toLowerCase().contains("rxtx") ){
				log.debug("Selected lib: {}", libName);
				serial.selectLibByName(libName);
				return true;
			}
		}
		
		log.error("Could not load rxtx library.");
		return false;
	}
	
	/**
	 * Auto selecting device.
	 * @return	{@code true} if successfully, {@code false} otherwise.
	 */
	private boolean autoSelectDevice(){
		for( SerialDevice device : serial.getAvailableDevices() ){
			
			log.info("Connecting to {} ...", device);
			
			if( serial.connect(device) ){	
				
				int vTries = 0;
				
				// try to connect
				while( vTries < CONNECT_MAX_TRIES  ){
					serial.write(DUMMY_REQUEST);
					
					try{
						if( read() == DUMMY_REQUEST){
							log.info("Connected!");
							connected = true;
							return true;
						}
					} catch(NoSuchElementException err){
						log.warn("Connection request to {} timed out.", device);
						
						try {
							Thread.sleep(CONNECT_TRIES_DELAY);
						} catch (InterruptedException e) {}
					}
				}
				
				log.error("Could not connect to {}", device);
			}
		}		
		
		return false;
	}
	
	/**
	 * Tries to read a data byte from buffer or throws Execption if timed out.
	 * @return		First data {@link Byte} from buffer
	 * @throws NoSuchElementException
	 */
	private byte read() throws NoSuchElementException{
		return read(false);
	}
	
	/**
	 * Tries to read a data byte from buffer or throws Execption if timed out.
	 * @param longTimeout	{@link Boolean} to switch between normal timeout {@code false}
	 * 				or multiple timeout {@code true} time. 
	 * @return		First data {@link Byte} from buffer
	 * @throws NoSuchElementException
	 */
	private synchronized byte read(boolean longTimeout) throws NoSuchElementException{
		long t1 = System.currentTimeMillis();
		
		while( System.currentTimeMillis()-t1 < TIMEOUT
				|| (longTimeout && System.currentTimeMillis()-t1 < TIMEOUT*TIMEOUT_MAX_MULTIPLIER) ){
			synchronized (dataBuffer) {
				try{
					return dataBuffer.pop();
				} catch( NoSuchElementException err ){}
			}
		}
		
		throw new NoSuchElementException("Timed out");
	}

	@Override
	public void serialDataRecieved(byte data) {
		synchronized (dataBuffer) {
			dataBuffer.add(data);
		}
	}
	
	
	
	/**
	 * Write data to servo
	 * @param aServo	{@link Servos} to write data to.
	 * @param aValue	{@link Byte} value to write to servo.
	 * @return			{@code true} if successful, {@code false} otherwise.
	 */
	public boolean setServoPosition(Servos aServo, byte aValue){
		if( isConnected() ){
			try{
				serial.write( PROTOCOL_START );
				serial.write( aServo.dataByte );
				serial.write( aValue );
				if( read() == aServo.dataByte  ){
					return true;
				}
			} catch (NoSuchElementException e){}
		}
		
		return false;
	}	
	
	/**
	 * Open Dummys hand
	 * @return	{@code true} if successful, {@code false} otherwise.
	 */
	public boolean openHand(){
		if( isConnected() ){
			try{
				serial.write( PROTOCOL_START );
				serial.write( Commands.OPEN_HAND.dataByte );
				return read() == Commands.OPEN_HAND.dataByte;
			} catch (NoSuchElementException e){}
		}
		return false;
	}
	
	/**
	 * Close Dummys hand
	 * @return	{@code true} if successful, {@code false} otherwise.
	 */
	public boolean closeHand(){
		if( isConnected() ){
			try{
				serial.write( PROTOCOL_START );
				serial.write( Commands.CLOSE_HAND.dataByte );
				return read() == Commands.CLOSE_HAND.dataByte;
			} catch (NoSuchElementException e){}
		}
		return false;
	}
	
	/**
	 * Sets Dummy into a predefined position
	 * @param aPosition	{@link Positions} to set Dummy into
	 * @return			{@code true} if successful, {@code false} otherwise.
	 */
	public boolean setDummyPosition(Positions aPosition){
		if( isConnected() ){
			try{
				serial.write( PROTOCOL_START );
				serial.write( aPosition.dataByte );
				return read() == aPosition.dataByte;
			} catch (NoSuchElementException e){}
		}
		return false;
	}
	
	/**
	 * Sets servo speed low {@link Byte}
	 * @param aValue	{@link BigInteger} speed value
	 * @return			{@code true} if successful, {@code false} otherwise.
	 */
	public boolean setServoSpeed(BigInteger aValue){
		if( isConnected() ){
			try{			
				serial.write( PROTOCOL_START );
				serial.write( Commands.SET_SERVO_SPEED.dataByte );
				serial.write( aValue.shiftRight(24).byteValue() );
				serial.write( aValue.shiftRight(16).byteValue() );
				serial.write( aValue.shiftRight(8).byteValue() );
				serial.write( aValue.byteValue() );
				if( read() == Commands.SET_SERVO_SPEED.dataByte ){
					return true;
				}
			} catch (NoSuchElementException e){}
			
		}
		return false;
	}
	
	/**
	 * Gets servo speed
	 * @return	Lowest {@link Byte} of servo speed
	 */
	public BigInteger getServoSpeed(){
		if( isConnected()){
			try{			
				serial.write( PROTOCOL_START );
				serial.write( Commands.GET_SERVO_SPEED.dataByte );
				byte b0 = read();
				byte b1 = read();
				byte b2 = read();
				byte b3 = read();				
				BigInteger b = new BigInteger(
						new byte[]{b3, b2, b1, b0});
				
				if( read() == Commands.GET_SERVO_SPEED.dataByte ){
					return b;
				}
			} catch (NoSuchElementException e){}
			
		}
		return new BigInteger("0");
	}
	
}
