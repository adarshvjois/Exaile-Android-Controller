package com.avjois.BT;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class ExaileControlActivity extends Activity {

	private BluetoothDevice dev;
	private BluetoothAdapter mBTadapter;
	private BluetoothSocket mbtSock;
	private InputStream ins;
	private OutputStream outs;
	private String DeviceName;
	private String DevName[];
	private UUID myID;
	private String TAG="ExaileControlActivity";
	
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    setContentView(R.layout.player_control);
	    Intent intent=getIntent();
	    DeviceName=intent.getStringExtra("device_address");
	    if(DeviceName.equals("No devices Paired")){
	    	finish();
	    }
	    Log.d("ExaileControlActivity","Device Name string="+DeviceName);	   
	    DevName=DeviceName.split("\n");
	    Log.d("ExaileControlActivity","Device Name string="+DevName[0]+"device address="+DevName[1]);
	    
	    mBTadapter=BluetoothAdapter.getDefaultAdapter();
	    myID=UUID.fromString("a9befec4-af8d-11e0-8272-001f3c68aa23");
	    dev=mBTadapter.getRemoteDevice(DevName[1]);
	    try {
			//mbtSock=dev.createRfcommSocketToServiceRecord(myID);
	    	mbtSock=dev.createInsecureRfcommSocketToServiceRecord(myID);
	    	Log.d(TAG,"Connecting...");
			mbtSock.connect();
			outs=mbtSock.getOutputStream();
			Log.d(TAG,"Success");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.d(TAG,"Fail"+e.toString());


			Context context = getApplicationContext();
			CharSequence text = getText(R.string.toast_msg);
			int duration = Toast.LENGTH_LONG;

			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
			finish();
		}		
	    
	    Button play_button=(Button) findViewById(R.id.play);
	    play_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				pauseExaile();
			}
		});
	    Button prev_button=(Button) findViewById(R.id.prev);
	    prev_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				prevExaile();
			}
		});
	    Button next_button=(Button) findViewById(R.id.next);
	    next_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				nextExaile();
			}
		});
	    Button volume_up=(Button) findViewById(R.id.volume_up);
	    volume_up.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				volumeIncrease();
			}
		});
	    Button volume_down=(Button) findViewById(R.id.volume_down);
	    volume_down.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				volumeDecrease();
			}
		});
	    Button exit_button=(Button) findViewById(R.id.exit);
	    exit_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				exitExaile();
			}
		});	    
	}
	public void pauseExaile(){
		String pause="PAUSE";
		byte msg[]=new byte[1024];
		msg=pause.getBytes();
		try{
			outs.write(msg);
		}catch(IOException e){
			Log.d(TAG,"Fail at sending the msg..check if connected to device"+e.toString());
			finish();
		}
	}
	
	public void prevExaile(){
		String pause="PREV";
		byte msg[]=new byte[1024];
		msg=pause.getBytes();
		try{
			outs.write(msg);
		}catch(IOException e){
			Log.d(TAG,"Fail at sending the msg..check if connected to device"+e.toString());
			e.printStackTrace();
			finish();
		}
	}

	public void nextExaile(){
		String pause="NEXT";
		byte msg[]=new byte[1024];
		msg=pause.getBytes();
		try{
			outs.write(msg);
		}catch(IOException e){
			Log.d(TAG,"Fail at sending the msg..check if connected to device"+e.toString());
			e.printStackTrace();
			finish();
		}
	}
	
	public void volumeIncrease(){
		String pause="VOL_UP";
		byte msg[]=new byte[1024];
		msg=pause.getBytes();
		try{
			outs.write(msg);
		}catch(IOException e){
			Log.d(TAG,"Fail at sending the msg..check if connected to device"+e.toString());
			e.printStackTrace();
			finish();
		}
	}
	public void volumeDecrease(){
		String pause="VOL_DOWN";
		byte msg[]=new byte[1024];
		msg=pause.getBytes();
		try{
			outs.write(msg);
		}catch(IOException e){
			Log.d(TAG,"Fail at sending the msg..check if connected to device"+e.toString());
			e.printStackTrace();
			finish();
		}
	}
	public void exitExaile(){
		String pause="EXIT";
		byte msg[]=new byte[1024];
		msg=pause.getBytes();
		try{
			outs.write(msg);
			Log.d(TAG,"Exitting the app..");
			mbtSock.close();
			finish();
		}catch(IOException e){
			Log.d(TAG,"Fail at sending the msg..check if connected to device"+e.toString());
			e.printStackTrace();
			finish();
		}
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();

		// Make sure we're not doing discovery anymore
		if (mBTadapter != null) {
			mBTadapter.cancelDiscovery();
		}

	}

}