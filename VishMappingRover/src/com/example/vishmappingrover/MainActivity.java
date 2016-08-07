//**************************************************************************//
// 	//
//**************************************************************************//
package com.example.vishmappingrover;

//
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

//Main Activity
public class MainActivity extends ActionBarActivity {
	
	private BluetoothSocket myBTRoverSocket=null; // Initialising Bluetooth socket 
	private DataOutputStream myBTRoverDataOPS = null; // Initialising data output stream for Bluetooth
	private DataInputStream myBTRoverDataIPS =null; // Initialising data output stream for Bluetooth
	private boolean started=false; // variable to check if the button has been started and to do the function accordingly
	private BluetoothDevice myBTRoverDevice=null; // Initialising Bluetooth Device
	private char readCharForMap='u';
	private float ElapsedTime=0;
	private float[] distances= new float[100];
	private int i=0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
                        
    }
	// method for connecting to the bluetooth rover which is paired with android device 
	public void Connect (View view){
		BluetoothAdapter blueToothRoverAdapter = BluetoothAdapter.getDefaultAdapter();
		Set<BluetoothDevice> btBondedRover = blueToothRoverAdapter.getBondedDevices();
		try{
			for(BluetoothDevice myBTDevice : btBondedRover){
			if(myBTDevice.getName().equals("Bluetooth_Bee_V2")){
				
				myBTRoverDevice= myBTDevice;
				Toast.makeText(this, "Connected to Rover", Toast.LENGTH_SHORT).show();
				break;
			}
		}
			myBTRoverSocket = myBTRoverDevice.createInsecureRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
			myBTRoverSocket.connect();
			
			myBTRoverDataOPS = new DataOutputStream(myBTRoverSocket.getOutputStream());
			myBTRoverDataIPS = new DataInputStream(myBTRoverSocket.getInputStream());
		}
		catch(IOException e){
			Toast.makeText(this, "Not Connected", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}        
	}
	
	//method for on-click of start button
	public void Start(View view) throws IOException{
		
		started=true;
		try{
			if(started)
			{
				try{
					myBTRoverDataOPS.write('s');
					Toast.makeText(this, "Start Command Sent", Toast.LENGTH_SHORT).show();				
				}
				catch(Exception e){
					Toast.makeText(this, "Not able to send start command", Toast.LENGTH_SHORT).show();
					
				}
				MapThread.start();		
			}
		}
		catch(Exception e)
		{
			Toast.makeText(this, "Please check the connection", Toast.LENGTH_SHORT).show();
		}
		
		
	}
	//method for on-click of stop button
	public void Stop(View view) throws IOException{
		try{
				myBTRoverDataOPS.write('t');
				Toast.makeText(this, "Stopped", Toast.LENGTH_SHORT).show();
				started=false;
		}
		catch(Exception e){
			Toast.makeText(this, "Not able to send stop command", Toast.LENGTH_SHORT).show();
			Toast.makeText(this, "Please check the connection", Toast.LENGTH_SHORT).show();
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
    
    Handler MapHandler= new Handler();
    //To receive data from rover for mapping
    Runnable charAquizition = new Runnable(){
    	public void run(){
    		try {
    			readCharForMap = myBTRoverDataIPS.readChar();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}    		
    	}    	
    };
    //method to convert elapsed time to distance array
    Runnable TimeToSpaceArray = new Runnable(){
    	public void run(){
    		float pixeldistance=ElapsedTime*2;
    		if (i>100){
    			i=0;
    		}
    		i=i+1;
    		distances[i]=pixeldistance;
    	}
    };
    //method to draw the line
    Runnable Mapping = new Runnable(){
    	public void run(){
    		Paint myPaint = new  Paint();
    		myPaint.setColor(Color.parseColor("Black"));
    		Bitmap myBitMap = Bitmap.createBitmap(480, 800, Bitmap.Config.ARGB_8888);
    		Canvas  myCanvas = new Canvas(myBitMap);
    	
    		for (int j=1;j<=98;j++) {
    			myCanvas.drawLine(distances[j], distances[j-1], distances[j+1], distances[j], myPaint);
    		} 				
    	}
    };
    
    Thread MapThread = new Thread()
    {
    	public void run(){
    		MapHandler.post(charAquizition);
    		try {
				sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		float startTime = System.currentTimeMillis();
    		
    		while(readCharForMap != 'y')
    		{
    			MapHandler.post(charAquizition);	
    		}
    		ElapsedTime = (System.currentTimeMillis() - startTime)/1000;
    		MapHandler.post(TimeToSpaceArray);
    		MapHandler.post(Mapping);
    	}
    };

}
