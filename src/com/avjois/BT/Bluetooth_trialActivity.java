package com.avjois.BT;

import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class Bluetooth_trialActivity extends Activity {

	// debug
	private static final String TAG = "DeviceListActivity";
	private static final boolean D = true;

	// Member fields
	private BluetoothAdapter mBtAdapter;
	private ArrayAdapter<String> mPairedDevicesArrayAdapter;
	private ArrayAdapter<String> mNewDevicesArrayAdapter;
	private BluetoothDevice selected_dev;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Button scanButton = (Button) findViewById(R.id.button_scan);

		scanButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				doDiscovery();
				//v.setVisibility(View.GONE);
			}

		});

		mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this,
				R.layout.device_name);
		mNewDevicesArrayAdapter = new ArrayAdapter<String>(this,
				R.layout.device_name);

		ListView pairedListView = (ListView) findViewById(R.id.paired_devices);
		pairedListView.setAdapter(mPairedDevicesArrayAdapter);
		pairedListView.setOnItemClickListener(mDeviceClickListener);

		ListView newDevicesListView = (ListView) findViewById(R.id.new_devices);
		newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
		newDevicesListView.setOnItemClickListener(mDeviceClickListener);

		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		this.registerReceiver(mReceiver, filter);

		// Register for broadcasts when discovery has finished
		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		this.registerReceiver(mReceiver, filter);

		// Get the local Bluetooth adapter
		mBtAdapter = BluetoothAdapter.getDefaultAdapter();
		if (!mBtAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 5);
         }
		// Get a set of currently paired devices
		Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

		// If there are paired devices, add each one to the ArrayAdapter
		if (pairedDevices.size() > 0) {
			findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
			for (BluetoothDevice device : pairedDevices) {
				mPairedDevicesArrayAdapter.add(device.getName() + "\n"
						+ device.getAddress());
			}
		} else {
			String noDevices = getResources().getText(R.string.none_paired)
					.toString();
			mPairedDevicesArrayAdapter.add(noDevices);
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		// Make sure we're not doing discovery anymore
		if (mBtAdapter != null) {
			mBtAdapter.cancelDiscovery();
		}

		// Unregister broadcast listeners
		this.unregisterReceiver(mReceiver);
	}

	/**
	 * Start device discover with the BluetoothAdapter
	 */
	private void doDiscovery() {
		if (D)
			Log.d(TAG, "doDiscovery()");
		mBtAdapter = BluetoothAdapter.getDefaultAdapter();
		if (!mBtAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 5);
         }

		// Indicate scanning in the title
		setProgressBarIndeterminateVisibility(true);
		setTitle(R.string.scanning);

		// Turn on sub-title for new devices
		findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);

		// If we're already discovering, stop it
		if (mBtAdapter.isDiscovering()) {
			mBtAdapter.cancelDiscovery();
		}

		// Request discover from BluetoothAdapter
		mBtAdapter.startDiscovery();
		//setContentView(R.layout.main);
		Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

		// If there are paired devices, add each one to the ArrayAdapter
		String noDevices = getResources().getText(R.string.none_paired)
		.toString();
		if (pairedDevices.size() > 0) {
			mPairedDevicesArrayAdapter.remove(noDevices);
			for(BluetoothDevice device:pairedDevices){
				mPairedDevicesArrayAdapter.remove(device.getName() + "\n"
						+ device.getAddress());	
			}
			
			findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
			for (BluetoothDevice device : pairedDevices) {
				mPairedDevicesArrayAdapter.add(device.getName() + "\n"
						+ device.getAddress());
			}
		} else {
			//String noDevices = getResources().getText(R.string.none_paired)
//					.toString();
			mPairedDevicesArrayAdapter.add(noDevices);
		}

	}

	private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
			// TODO Auto-generated method stub
			mBtAdapter.cancelDiscovery();

			// Get the device MAC address, which is the last 17 chars in the
			// View
			String info = ((TextView) v).getText().toString();
			String address = info;
			if(address.equals("No devices Paired")){
				return;
			}
			if(D){
				Log.d(TAG,"Address>>"+address+">>"+info.length());
			}
			// Create the result Intent and include the MAC address
			Intent intent = new Intent();
			intent.putExtra("device_address", address);

			// Set result and finish this Activity
			setResult(Activity.RESULT_OK, intent);
			Intent exaileControl=new Intent(v.getContext(),ExaileControlActivity.class);
			exaileControl.putExtra("device_address",address);
			startActivityForResult(exaileControl,0);
			finish();
			}
	};

	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			// When discovery finds a device
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				// Get the BluetoothDevice object from the Intent
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				// If it's already paired, skip it, because it's been listed
				// already
				if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
					mNewDevicesArrayAdapter.remove("No devices found");

					mNewDevicesArrayAdapter.remove(device.getName() + "\n"
							+ device.getAddress());
					mNewDevicesArrayAdapter.add(device.getName() + "\n"
							+ device.getAddress());
				}
				// When discovery is finished, change the Activity title
			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
					.equals(action)) {
				setProgressBarIndeterminateVisibility(false);
				setTitle(R.string.select_device);

				setTitle(R.string.select_device);
				if (mNewDevicesArrayAdapter.getCount() == 0) {
					String noDevices = getResources().getText(
							R.string.none_found).toString();
					
					mNewDevicesArrayAdapter.remove(noDevices);
					mNewDevicesArrayAdapter.add(noDevices);
				}
			}
		}
	};

}