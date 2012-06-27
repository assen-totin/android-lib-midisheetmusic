package com.voody.midisheet;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.LinearLayout;


public class SheetMusicEntryPoint extends Activity {
    private SheetMusic sheet;    /* The sheet music */
    private MidiFiles midifile;   /* The midi file to play */
    private MidiOptions options; /* The options for sheet music and sound */
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	    
    	Bundle recvBundle = this.getIntent().getExtras();
    	byte[] data = recvBundle.getByteArray("data");
    	
        ClefSymbol.LoadImages(this);
        TimeSigSymbol.LoadImages(this);    
        
        midifile = new MidiFiles(data);
        options = new MidiOptions(midifile);

        sheet = new SheetMusic(this);
        sheet.init(midifile, options);
    
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
               
        layout.addView(sheet);       
        setContentView(layout);       	
       
        sheet.callOnDraw();
        sheet.keepRunning();
        
        Handler scrollTimer = new Handler();
        scrollTimer.postDelayed(flingScrollFinish, 60000);
    }
    
    Runnable flingScrollFinish = new Runnable() {
        public void run() {
        	finish();
        }
    };
}
