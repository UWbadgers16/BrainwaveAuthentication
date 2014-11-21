package com.example.brainwaveauthentication;

import java.util.Arrays;
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
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

	private UsbDevice usbDevice;
	private UsbManager usbManager;
	private UsbInterface usbInterface;
	private UsbEndpoint usbEndpoint;
	private UsbDeviceConnection usbConnection;
	private byte[] data = new byte[32];
	private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
	private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {

	    public void onReceive(Context context, Intent intent) {
	        String action = intent.getAction();
	        if (ACTION_USB_PERMISSION.equals(action)) {
	            synchronized (this) {
	                usbDevice = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

	                if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
	                    if(usbDevice != null){
	                    	connectionSetup();
	                   }
	                } 
	                else {
	                	Toast.makeText(getApplicationContext(), "Permission denied for device", Toast.LENGTH_SHORT).show();
	                }
	            }
	        }
	    }
	};
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
		usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        usbDevice = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
        if(usbDevice != null) {
        	Toast.makeText(getApplicationContext(), "Emotiv device detected", Toast.LENGTH_SHORT).show();
        	connectionSetup();
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
    	if(usbDevice == null) {
        	getDevice();
        	if(usbDevice != null) {
            	PendingIntent mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
            	IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
            	registerReceiver(usbReceiver, filter);
            	usbManager.requestPermission(usbDevice, mPermissionIntent);
    	    	Toast.makeText(getApplicationContext(), Arrays.toString(read()), Toast.LENGTH_SHORT).show();
        	}
    	}
    	else {
    		read();
    		Toast.makeText(getApplicationContext(), Arrays.toString(read()), Toast.LENGTH_SHORT).show();
    	}
    }
    
    /* Gets the USB device handle */
    private void getDevice() {
    	HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
    	Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
    	boolean found = false;
    	while(deviceIterator.hasNext() && !found){
    		usbDevice = deviceIterator.next();
    	    if(usbDevice.getProductId() == 60674) {
    	    	found = true;
    	    	Toast.makeText(getApplicationContext(), "Emotiv device detected", Toast.LENGTH_SHORT).show();
    	    }
    	}
    }
    
    /* Set up USB connection */
    private void connectionSetup() {
    	usbInterface = usbDevice.getInterface(1);
    	usbEndpoint = usbInterface.getEndpoint(0);
    	usbConnection = usbManager.openDevice(usbDevice);
    	usbConnection.claimInterface(usbInterface, true);
    }
    
    /* Perform a read from USB device */
    private byte[] read() {
    	Thread thread = new Thread(new Runnable() {
    		@Override
    		public void run() {
    	    	Toast.makeText(getApplicationContext(), "Starting read", Toast.LENGTH_SHORT).show();
    	    	usbConnection.bulkTransfer(usbEndpoint, data, data.length, 0);
    		}
    	});
    	
    	thread.start();
    	return data;
    }
}
