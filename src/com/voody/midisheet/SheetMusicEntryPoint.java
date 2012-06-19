package com.voody.midisheet;

//import android.os.*;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.*;
//import android.app.Activity;
import android.app.Activity;
//import android.content.Context;
//import android.content.res.Resources;
import android.os.Bundle;

//import java.io.IOException;
//import java.io.InputStream;

public class SheetMusicEntryPoint extends Activity {
    private SheetMusic sheet;    /* The sheet music */
    //private LinearLayout layout; /* The layout */
    private MidiFile midifile;   /* The midi file to play */
    private MidiOptions options; /* The options for sheet music and sound */

    //private Resources resources;
    //Context context;
    
    public void onCreate() {
    	Bundle recvBundle = this.getIntent().getExtras();
    	byte[] data = recvBundle.getByteArray("data");
    	
        ClefSymbol.LoadImages(this);
        TimeSigSymbol.LoadImages(this);    
       		
        midifile = new MidiFile(data);
        options = new MidiOptions(midifile);

        sheet = new SheetMusic(this);
        sheet.init(midifile, options);
    	
        /*
		ViewGroup parent = (ViewGroup) view.getParent();
		int index = parent.indexOfChild(view);
		parent.removeView(view);
		view = getLayoutInflater().inflate(sheet, parent, false);
		parent.addView(view, index);
		*/

    	//layout = new LinearLayout(this);
        //layout.setOrientation(LinearLayout.VERTICAL);
        //layout.addView(sheet);       
        //setContentView(layout);
        //layout.requestLayout();

        sheet.callOnDraw();
        sheet.keepRunning();
    }
}
