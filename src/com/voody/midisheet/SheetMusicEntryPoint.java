package com.voody.midisheet;


import android.app.Activity;
import android.content.Context;
//import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class SheetMusicEntryPoint extends Activity {
    private SheetMusic sheet;    /* The sheet music */
    private MidiFiles midifile;   /* The midi file to play */
    private MidiOptions options; /* The options for sheet music and sound */

    Context context;
    
    public void onCreate() {
    	Bundle recvBundle = this.getIntent().getExtras();
    	byte[] data = recvBundle.getByteArray("data");
    	
        ClefSymbol.LoadImages(this);
        TimeSigSymbol.LoadImages(this);    
       		
        midifile = new MidiFiles(data);
        options = new MidiOptions(midifile);

        sheet = new SheetMusic(this);
        sheet.init(midifile, options);
        
        LayoutInflater inflater = getLayoutInflater();
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.main, null); 
        View view = layout.findViewById(R.id.sheet_music);
        ViewGroup parent = (ViewGroup) view.getParent();
		int index = parent.indexOfChild(view);
		parent.removeView(view);
		//view = inflater.inflate(sheet, parent, false);
		//parent.addView(view, index);
		parent.addView(sheet, index);
	
    	//layout = new LinearLayout(this);
        //layout.setOrientation(LinearLayout.VERTICAL);
        //layout.addView(sheet);       
        //setContentView(layout);
        //layout.requestLayout();

        sheet.callOnDraw();
        sheet.keepRunning();
        
        System.err.println("Reached finish in second activity.");
        
        finish();
    }
}
