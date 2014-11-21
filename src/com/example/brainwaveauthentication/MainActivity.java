package com.example.brainwaveauthentication;

import java.util.HashMap;
import java.util.Iterator;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

	private UsbDevice device;
	private UsbManager usbManager;
	private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
	private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

	    public void onReceive(Context context, Intent intent) {
	    	Toast.makeText(getApplicationContext(), "WE'RE DOING THIS STUFF NOW", Toast.LENGTH_SHORT).show();
	        String action = intent.getAction();
	        if (ACTION_USB_PERMISSION.equals(action)) {
	            synchronized (this) {
	                device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

	                if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
	                    if(device != null){
	                      //call method to set up device communication
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
        device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
        if(device != null)
        	Toast.makeText(getApplicationContext(), "Emotiv device detected", Toast.LENGTH_SHORT).show();
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
    	if(device == null) {
    		usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        	getDevice();
        	if(device != null) {
            	PendingIntent mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
            	IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
            	registerReceiver(mUsbReceiver, filter);
            	usbManager.requestPermission(device, mPermissionIntent);
    	    	Toast.makeText(getApplicationContext(), "Permission requested", Toast.LENGTH_SHORT).show();
        	}
    	}
    }
    
    /* Gets the USB device handle */
    private void getDevice() {
    	HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
    	Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
    	boolean found = false;
    	while(deviceIterator.hasNext() && !found){
    	    device = deviceIterator.next();
    	    if(device.getProductId() == 60674) {
    	    	found = true;
    	    	Toast.makeText(getApplicationContext(), "Emotiv device detected", Toast.LENGTH_SHORT).show();
    	    }
    	}
    }
}
