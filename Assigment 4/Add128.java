/*
 * Stefon Miller
 * CS1501 Summer 2019
 * This class implements SymCipher and
 * provides functionality of an addition cipher
 */
import java.util.Random;

public class Add128 implements SymCipher 
{
	byte[] key;										//Key used for encryption/decryption
	
	/**
	 * Parameterless constructor
	 */
	public Add128()
	{
		//Initialize key to specified size
		key = new byte[128];
		//Fill each index in key with random bytes
		new Random().nextBytes(key);
	}
	
	/**
	 * Constructor w/ parameter for server
	 * @param b byte array from server
	 */
	public Add128(byte[] b)
	{
		key = b;
	}
	
	/**
	 * Gets byte array representing key
	 */
	public byte[] getKey() 
	{
		return key;
	}
	
	/**
	 * Encodes string into byte array and returns it
	 */
	public byte[] encode(String S) 
	{
		//Fill temp array with all char bytes in s
		byte[] temp = S.getBytes();
		//Test integer for end of key array
		int j = 0;
		//Encode temp by adding each byte in key to each of its indices
		for(int i = 0; i < temp.length; i++)
		{
			//Special case if message is longer than 128 chars, reset j to 0
			if(j == key.length)
			{
				j=0;
			}
			temp[i] += key[j];
			j++;
		}
		return temp;
	}
	
	/**
	 * Decodes byte array into string and returns it
	 */
	public String decode(byte[] bytes) 
	{
		//Test integer for end of key array
		int j = 0;
		//Same loop as encode, except we subtract
		for(int i = 0; i < bytes.length; i++)
		{
			if(j == key.length)
			{
				j=0;
			}
			bytes[i] -= key[j];
			j++;
		}
		//Convert byte array to string and return it
		String out = new String(bytes);
		return out;
	}
}
