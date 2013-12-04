/*
 *   smspdu - send raw PDU SMS from you computer using a HTC Android phone.
 *    send HEX encoded PDU to port 2323 of your phone running this application
 *   
 *   (c) Collin Mulliner
 *   http://www.mulliner.org/android/
 */

package org.mulliner.smspdu;

import org.mulliner.smspdu.R;

import java.lang.reflect.Method;
import java.net.*;
import android.app.Activity;
import android.app.PendingIntent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsManager;
import android.widget.TextView;

public class main extends Activity implements Runnable 
{
	private static String logmsg;
	private boolean running;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        logmsg = new String("");
        
        log("pdusms: send raw PDU SMS from your computer using a HTC Android phone. (c) Collin Mulliner, http://www.mulliner.org/android/\n");
        
        running = true;
        Thread t = new Thread(this);
        t.start();
    }
    
    private Handler handler = new Handler() {
    	@Override
    	public void handleMessage(Message msg) {
    		plog((String)msg.obj);
    	}
    };
    
    private void log(String l)
    {
    	Message m = new Message();
    	m.obj = l;
    	handler.sendMessage(m);
    }
    
    private void plog(String log)
    {
    	logmsg = log.concat(logmsg);
    	TextView t = new TextView(this);

        t = (TextView) findViewById(R.id.textView1); 
        t.setText(logmsg);
    }
    	    	 
	public static byte[] convertHexadecimal2Binary(byte[] hex)
	{
		String HEX_STRING  = "0123456789ABCDEF";
		int block = 0;
		byte[] data = new byte[hex.length / 2];
		int index = 0;
		boolean next = false;
		
		for (int i=0; i<hex.length; i++) {
			block <<= 4;
			int pos = HEX_STRING.indexOf(Character.toUpperCase((char) hex[i]));
			if (pos > -1) block += pos;
 
			if (next) {
				data[index] = (byte)(block & 0xff );
				index++;
				next = false;
			} 
			else
				next = true;
		}
		return data;
	}
    	
	public void run()
    {
		ServerSocket ss = null;
		Socket c;
		java.io.DataInputStream cdi;
		
		try {
			ss = new java.net.ServerSocket(2323, 0, InetAddress.getByName("0.0.0.0"));
		} catch (Exception e) {
			log(e.toString() + "\n");
		}
    		
		do {
			log("listening for connection on port 2323...\n");
			try {
				c = ss.accept();
			} catch (Exception e) { log(e.toString() + "\n"); continue; }
			
			log("connection accepted\n");
			log("waiting for PDU...\n");
	    		
			try {
				cdi = new java.io.DataInputStream(c.getInputStream());
			} catch (Exception e) { log(e.toString() + "\n"); continue; }
	    		
			try {
				Thread.sleep(2000);
			} catch (Exception e) {}
	    		
			try {
				if (cdi.available() > 20) {
					String msg = cdi.readLine();
					log("msg: " + msg + "\n");
					sendsmspdu(msg);
					
				}
				c.close();
				log("closed connection\n");
			} catch (Exception e) { log(e.toString() + "\n"); continue; }
	    		
    	} while (running);
    }
    	
    private void sendsmspdu(String msg)
    {
		try {
			  SmsManager sm = SmsManager.getDefault(); 
			  /*
			  // debug, print all methods in this class , there are 2 private methods in this class
			  Class c = sm.getClass();	  
			  Method[] ms = c.getDeclaredMethods();

			  for (int i = 0; i < ms.length; i++)
			    Log.w("ListMethos ", ms[i].toString() );
			  */
			  
			  // get private method
			  byte[] bb = new byte[1];
			  Method m2 = SmsManager.class.getDeclaredMethod("sendRawPdu",bb.getClass(),bb.getClass(),PendingIntent.class,PendingIntent.class,boolean.class,boolean.class);
			  //log("success getting sendRawPdu\n");
			  m2.setAccessible(true);
			  
			  log("sending message!\n");
			  byte pdu[] = convertHexadecimal2Binary(msg.getBytes());
			  m2.invoke(sm, null, pdu, null, null, false, false );

		} catch (Exception e) { e.printStackTrace(); } 
    }
}