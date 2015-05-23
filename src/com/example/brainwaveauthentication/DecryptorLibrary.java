package com.example.brainwaveauthentication;

public class DecryptorLibrary {

	public native String jniActivate();
	public native boolean jniUnscramble();
	
	static
	{
        /* Loading the decryption library */
    	System.loadLibrary("androidEmotivDecryptor");
	}
}
