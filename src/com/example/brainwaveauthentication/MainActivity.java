package com.example.brainwaveauthentication;

import java.util.HashMap;
import java.util.Iterator;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/* Main activity */
public class MainActivity extends ActionBarActivity {

	/* Declare USB constructs */
	private UsbDevice usbDevice;
	private UsbManager usbManager;
	private UsbInterface usbInterface;
	private UsbEndpoint usbEndpoint;
	private UsbDeviceConnection usbConnection;
	
	/* 32-byte data read from Emotiv EPOC */
	private byte[] data = new byte[32];
	
	/* Used for PermissionIntent */
	private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
	
	/* Loading the decyprtion library */
	static {
		System.loadLibrary("libdecryptor");
	}
	
	/* BroadcastReceiver for USB device permission */
	private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {

		//when intent is received
	    public void onReceive(Context context, Intent intent) {
	        String action = intent.getAction();
	        if (ACTION_USB_PERMISSION.equals(action)) {
	            synchronized (this) {
	                usbDevice = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);				//get the UsbDevice
	                
	                //if the permission was granted
	                if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
	                	//if actual USB device
	                    if(usbDevice != null){
	                    	connectionSetup();																//setup the USB connection
	                		read();																			//perform the initial read
	                		populateData();																	//populate the Text Views
	                   }
	                } 
	                else {
	                	Toast.makeText(getApplicationContext(), "Permission denied for device", Toast.LENGTH_SHORT).show();
	                }
	            }
	        }
	    }
	};
	
	/* When MainActivity is created */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Intent intent = getIntent();																		//get the incoming intent
		usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);									//instantiate the UsbManager
        usbDevice = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);							//get the UsbDevice
        
        //if actual USB device
        if(usbDevice != null) {
        	Toast.makeText(getApplicationContext(), "Emotiv device detected", Toast.LENGTH_SHORT).show();	//display that Emotiv is connected
        	connectionSetup();																				//setup the USB connection
        }
        
        
    }
    @Override
    
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    /* Called when the user clicks the Sample button */
    public void sample(View view) {
    	//if the USB device hasn't yet been setup
    	if(usbDevice == null) {
        	getDevice();																					//get the UsbDevice
        	
        	//if we got the USB device
        	if(usbDevice != null) {
        		//request permission to use the USB device
            	PendingIntent usbPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
            	IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
            	registerReceiver(usbReceiver, filter);
            	usbManager.requestPermission(usbDevice, usbPermissionIntent);
        	}
    	}
    	//the device has been setup
    	else {
    		read();																							//perform read
    		populateData();																					//populate the Text Views
    	}
    }
    
    /* Gets the USB device handle */
    private void getDevice() {
    	HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();									//get all USB devices
    	Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();								//instantiate device iterator
    	boolean found = false;	
    	
    	//loop until Emotiv device is found
    	while(deviceIterator.hasNext() && !found){	
    		usbDevice = deviceIterator.next();
    		
    		//matches Emotiv device
    	    if(usbDevice.getProductId() == 60674) {
    	    	found = true;
    	    	Toast.makeText(getApplicationContext(), "Emotiv device detected", Toast.LENGTH_SHORT).show(); //display that Emotiv is connected
    	    }
    	}
    }
    
    /* Set up USB connection */
    private void connectionSetup() {
    	usbInterface = usbDevice.getInterface(1);															//instantiate proper interface
    	usbEndpoint = usbInterface.getEndpoint(0);															//instantiate proper endpoint
    	usbConnection = usbManager.openDevice(usbDevice);													//establish a UsbConnection
    	usbConnection.claimInterface(usbInterface, true);													//claim the interface on the connectoin
    }
    
    /* Perform a read from USB device */
    private byte[] read() {
    	
    	//create thread to perform transfer so as not to disturb UI thread
    	new Thread(new Runnable() {
    		
    		//run thread
    		@Override
    		public void run() {
    	        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);		//move current thread into background
    	    	usbConnection.bulkTransfer(usbEndpoint, data, data.length, 0);								//perform a transfer from USB device
    		}
    	}).start();																							//start the thread
    	
    	return data;
    }
    
    /* Display some data to the text views */
    private void populateData() {
    	TextView textView = (TextView) findViewById(R.id.channel_one);										//finds the specific Text View
    	textView.setText(Integer.toString(twoBytes(data[1], data[2])));										//displays concatenated 2-byte data
    	textView = (TextView) findViewById(R.id.channel_two);
    	textView.setText(Integer.toString(twoBytes(data[3], data[4])));
    	textView = (TextView) findViewById(R.id.channel_three);
    	textView.setText(Integer.toString(twoBytes(data[5], data[6])));
    	textView = (TextView) findViewById(R.id.channel_four);
    	textView.setText(Integer.toString(twoBytes(data[7], data[8])));
    	textView = (TextView) findViewById(R.id.channel_five);
    	textView.setText(Integer.toString(twoBytes(data[9], data[10])));
    	textView = (TextView) findViewById(R.id.channel_six);
    	textView.setText(Integer.toString(twoBytes(data[11], data[12])));
    	textView = (TextView) findViewById(R.id.channel_seven);
    	textView.setText(Integer.toString(twoBytes(data[13], data[15])));
    	textView = (TextView) findViewById(R.id.channel_eight);
    	textView.setText(Integer.toString(twoBytes(data[16], data[17])));
    	textView = (TextView) findViewById(R.id.channel_nine);
    	textView.setText(Integer.toString(twoBytes(data[18], data[19])));
    	textView = (TextView) findViewById(R.id.channel_ten);
    	textView.setText(Integer.toString(twoBytes(data[20], data[21])));
    	textView = (TextView) findViewById(R.id.channel_eleven);
    	textView.setText(Integer.toString(twoBytes(data[22], data[23])));
    	textView = (TextView) findViewById(R.id.channel_twelve);
    	textView.setText(Integer.toString(twoBytes(data[24], data[25])));
    	textView = (TextView) findViewById(R.id.channel_thirteen);
    	textView.setText(Integer.toString(twoBytes(data[26], data[27])));
    	textView = (TextView) findViewById(R.id.channel_fourteen);
    	textView.setText(Integer.toString(twoBytes(data[30], data[31])));
    }
    
    /* Concatenate two bytes */
    private short twoBytes(byte one, byte two) {
    	return (short) ((one << 8) | two);
    }
}
