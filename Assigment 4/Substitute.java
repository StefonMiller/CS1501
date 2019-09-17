/*
 * Stefon Miller
 * CS1501 Summer 2019
 * This class implements SymCipher and provides
 * functionality of a substitution cipher
 */

import java.util.ArrayList;
import java.util.Collections;

public class Substitute implements SymCipher
{
	byte[] key;														//Key used for encoding/decoding
	byte[] inv;														//Inverse array of key
	/**
	 * Parameterless constructor
	 */
	public Substitute()
	{
		//Initialize key to specified value
		key = new byte[256];
		//Make inverse same size as key
		inv = new byte[key.length];
		//Create an arraylist and fill it with all numbers 0-key.length, then shuffle them
		ArrayList<Byte> a = new ArrayList<Byte>(key.length);
		for(int i = 0; i< key.length; i++)
		{
			a.add((byte)i);
		}
		Collections.shuffle(a);
		//Fill indices in key with those in the arraylist we shuffled
		for(int i = 0; i < key.length; i++)
		{
			key[i] = a.get(i);
			//Fill inverse with opposite values in key, note we must convert any negative indices
			//from signed bytes to unsigned integers
			Byte index = new Byte(key[i]);
			inv[Byte.toUnsignedInt(index)] = (byte)i;
		}
	}
	
	/**
	 * Constructor used by server
	 * @param b	byte array from server
	 */
	public Substitute(byte[] b)
	{
		//Set key to the byte array passed in and make the inverse array the same length
		key = b;
		inv = new byte[key.length];
		//Fill the inverse array with inverse indices of key, converting any negative indices to unsigned ints
		for(int i = 0; i < key.length; i++)
		{
			Byte index = new Byte(key[i]);
			inv[Byte.toUnsignedInt(index)] = (byte)i;
		}
	}

	/**
	 * Return key
	 */
	public byte[] getKey() 
	{
		return key;
	}
	
	/**
	 * Encode String into byte array and return it
	 */
	public byte[] encode(String S) 
	{
		//Initialize 2 byte arrays, one containing the String parameter and the other
		//to be used for holding the encrypted bytes
		byte[] temp = S.getBytes();
		byte[] build = new byte[temp.length];
		//Loop through temp and encode its bytes based on our key
		for(int i = 0; i < temp.length; i++)
		{
			//Current index is the current byte in temp
			Byte index = new Byte(temp[i]);
			//Set the index in our output array to the corresponding index as specified by our key array
			build[i] = key[Byte.toUnsignedInt(index)];
		}
		return build;
	}
	
	/**
	 * Decode byte array into string and return it
	 */
	public String decode(byte[] bytes) 
	{
		//Decoding array
		byte[] dec = new byte[bytes.length];
		//This loop has the same logic as the encoding method, but it uses bytes instead of temp and inv 
		//instead of key
        for (int i = 0; i < bytes.length; i++) 
        {
        	//Current index is the current byte in bytes
        	Byte index = new Byte(bytes[i]);
        	//Set the index in our decoded array to the corresponding index as specified by our inverse array
            dec[i] = (inv[Byte.toUnsignedInt(index)]);
        }
        return new String(dec);
	}
}
