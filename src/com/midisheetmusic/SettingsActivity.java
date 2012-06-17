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


package com.midisheetmusic;

import android.app.*;
import android.content.*;
import android.content.res.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import android.preference.*;


/** @class SettingsActivity
 *  This activity is created by the "Settings" menu option.
 *  The user can change settings such as:
 *  - Which tracks to display
 *  - Which tracks to mute
 *  - Which instruments to use during playback
 *  - Whether to scroll horizontally or vertically
 *  - Whether to display the piano or not
 *  - Whether to display note letters or not
 *  - Transpose the notes to another key
 *  - Change the key signature or time signature displayed
 *  - Change how notes are combined into chords (the time interval)
 *  - Whether to display measure numbers
 *  - Play selected measures in a loop
 * 
 * When created, pass an Intent parameter containing MidiOptions.
 * When destroyed, this activity passes the result MidiOptions to the Intent.
 */
public class SettingsActivity extends PreferenceActivity 
    implements Preference.OnPreferenceChangeListener {

    public static final String settingsID = "settings";

    private MidiOptions options;         /** The initial option values */


    /** Create the Settings activity. Retrieve the initial option values
     *  (MidiOptions) from the Intent.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("MidiSheetMusic: Settings");
        options = (MidiOptions) this.getIntent().getSerializableExtra(settingsID);
    }


    /** For each list dialog, we display the value selected in the "summary" text.
     *  When a new value is selected from the list dialog, update the summary
     *  to the selected entry.
     */

    public boolean onPreferenceChange(Preference preference, Object newValue) {
    	/*
        ListPreference list = (ListPreference) preference;
        int index = list.findIndexOfValue((String)newValue);
        CharSequence entry = list.getEntries()[index];
        preference.setSummary(entry);
        */
        return true;
    }

        
}
