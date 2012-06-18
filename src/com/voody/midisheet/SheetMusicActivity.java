/*
 * Copyright (c) 2011-2012 Madhav Vaidyanathan
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 2.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 */

package com.voody.midisheet;

import android.os.*;
import android.widget.*;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;

import java.io.IOException;
import java.io.InputStream;

/** @class SheetMusicActivity
 *
 * The SheetMusicActivity is the main activity. The main components are:
 * - MidiPlayer : The buttons and speed bar at the top.
 * - Piano : For highlighting the piano notes during playback.
 * - SheetMusic : For highlighting the sheet music notes during playback.
 *
 */
public class SheetMusicActivity extends Activity {

    
    private SheetMusic sheet;    /* The sheet music */
    private LinearLayout layout; /* THe layout */
    private MidiFile midifile;   /* The midi file to play */
    private MidiOptions options; /* The options for sheet music and sound */

    private Resources resources;
    Context context;
    
     /** Create this SheetMusicActivity.  The Intent should have two parameters:
      * - MidiTitleID: The title of the song (String)
      * - MidiDataID: The raw byte[] data of the midi file.
      */
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        ClefSymbol.LoadImages(this);
        TimeSigSymbol.LoadImages(this);

        resources = this.getResources();
        InputStream iS = null;
        String fileName = "waltz";
        int rID = resources.getIdentifier("com.midisheetmusic:raw/"+fileName, null, null);
        try {
        	iS = resources.openRawResource(rID);
        } catch (android.content.res.Resources.NotFoundException e) {
			e.printStackTrace();
			finish();
		}
        
        byte[] data = null;
        try {
        data = new byte[iS.available()];
        iS.read(data);
        } catch (IOException e) {
        	e.printStackTrace();
        }
        		
        midifile = new MidiFile(data);
        options = new MidiOptions(midifile);
        createSheetMusic(options);
    }
    
    /** Create the SheetMusic view with the given options */
    private void 
    createSheetMusic(MidiOptions options) {
        sheet = new SheetMusic(this);
        sheet.init(midifile, options);
    	
    	layout = new LinearLayout(this);
        //layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(sheet);       
        setContentView(layout);
        layout.requestLayout();

        sheet.callOnDraw();
        sheet.keepRunning();
    }

}
