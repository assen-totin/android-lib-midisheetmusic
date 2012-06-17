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

import java.io.*;
import java.util.*;
import android.net.*;
import android.app.*;
import android.os.*;
import android.widget.*;
import android.util.Log;
import android.view.*;
import android.content.*;
import android.content.res.*;
import android.provider.*;
import android.database.*;
import android.graphics.*;
import android.text.*;

/** @class IconArrayAdapter
 *  The ListAdapter for displaying the list of songs.
 *  Similar to the array adapter, but adds a NotePair icon
 *  to the left side of each item displayed.
 */
class IconArrayAdapter<T> extends ArrayAdapter<T> {
    private LayoutInflater inflater;
    private static Bitmap notepair;    /* The notepair icon */

    /** Load the NotePair image into memory. */
    public void LoadImages(Context context) {
        if (notepair == null) {
            Resources res = context.getResources();
            notepair = BitmapFactory.decodeResource(res, R.drawable.notepair);
        }
    }

    /** Create a new IconArrayAdapter. Load the NotePair image */
    public IconArrayAdapter(Context context, int resourceId, List<T> objects) {
        super(context, resourceId, objects);
        LoadImages(context);
        inflater = LayoutInflater.from(context); 
    }

    /** Create a view for displaying a song in the ListView.
     *  The view consists of a Note Pair icon on the left-side,
     *  and the name of the song.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.choose_song_item, null);
         }
         TextView text = (TextView)convertView.findViewById(R.id.choose_song_name);
         ImageView image = (ImageView)convertView.findViewById(R.id.choose_song_icon);
         text.setText(this.getItem(position).toString());
         image.setImageBitmap(notepair);
         return convertView;
    }
}
    

/** @class ChooseSongActivity
 * The ChooseSongActivity class is used to display a list of
 * songs to choose from.  The list is created from the songs
 * shipped with MidiSheetMusic (in the assets directory), and 
 * also by searching for midi files in the internal/external 
 * device storage.
 *
 * When a song is chosen, this calls the SheetMusicAcitivty, passing
 * the raw midi byte[] data as a parameter in the Intent.
 */ 
public class ChooseSongActivity extends ListActivity implements TextWatcher {

    /** The complete list of midi files */
    ArrayList<FileUri> songlist;

    /** The list of midi files to display */
    ArrayList<FileUri> songlistDisplay;

    /** Textbox to filter the songs by name */
    EditText filterText;

    IconArrayAdapter<FileUri> adapter;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.choose_song);
    }

    /** Create the choose song activity. Add all the midi file resources
     *  into the song list. Search for additional midi files in the 
     *  internal/external storage, and also add those to the songlist.
     */ 
    @Override
    public void onResume() {
        super.onResume();
        setTitle("MidiSheetMusic: Choose Song");
        songlist = new ArrayList<FileUri>();

        loadAssetMidiFiles();

        // Find additional midi files in the internal/external storage
        loadMidiFilesFromStorage(MediaStore.Audio.Media.INTERNAL_CONTENT_URI);
        loadMidiFilesFromStorage(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);

        // Sort the songlist by name
        Collections.sort(songlist, songlist.get(0));

        // Set a custom adapter to display the NotePair icon on the left side
        adapter = new IconArrayAdapter<FileUri>(this, android.R.layout.simple_list_item_1, songlist);
        this.setListAdapter(adapter);

        filterText = (EditText) findViewById(R.id.name_filter);
        filterText.addTextChangedListener(this);
        filterText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
    }

    /** Load all the sample midi songs from the assets directory into songlist.
     *  Look for files ending with ".mid"
     */
    void loadAssetMidiFiles() {
        try {
            AssetManager assets = this.getResources().getAssets();
            String[] files = assets.list("");
            for (String path: files) {
                if (path.endsWith(".mid")) {
                    FileUri file = new FileUri(assets, path, path);
                    songlist.add(file);
                }
            }
        }
        catch (IOException e) {
        }
    }

    
    /** Look for midi files (with mime-type audio/midi) in the 
     * internal/external storage. Add them to the songlist.
     */
    private void loadMidiFilesFromStorage(Uri content_uri) {
        ContentResolver resolver = getContentResolver();
        String columns[] = { 
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE, 
            MediaStore.Audio.Media.MIME_TYPE 
        };
        String selection = MediaStore.Audio.Media.MIME_TYPE + " LIKE '%mid%'";
        Cursor cursor = resolver.query(content_uri, columns, selection, null, null);
        if (cursor == null) {
            return;
        }
        if (!cursor.moveToFirst()) {
            cursor.close();
            return;
        }
        
        do {
            int idColumn = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int titleColumn = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int mimeColumn = cursor.getColumnIndex(MediaStore.Audio.Media.MIME_TYPE);

            long id = cursor.getLong(idColumn);
            String title = cursor.getString(titleColumn);
            String mime = cursor.getString(mimeColumn);

            if (mime.endsWith("/midi") || mime.endsWith("/mid")) {
                Uri uri = Uri.withAppendedPath(content_uri, "" + id);
                FileUri file = new FileUri(resolver, uri, title);
                songlist.add(file);
            }
        } while (cursor.moveToNext());
        cursor.close();
    }
    
    /** When a song is clicked on, start a SheetMusicActivity.
     *  Read the raw byte[] data of the midi file.
     *  Pass the raw byte[] data as a parameter in the Intent.
     *  Pass the midi file Title as a parameter in the Intent.
     */
    @Override
    protected void onListItemClick(ListView parent, View view, int position, long id) {
        super.onListItemClick(parent, view, position, id);
        FileUri file = (FileUri) this.getListAdapter().getItem(position);
        byte[] data = file.getData();
        if (data == null || data.length <= 6 || !hasMidiHeader(data)) {
            showErrorDialog("Error: Unable to open song: " + file.toString());
            return;
        }
        Intent intent = new Intent(this, SheetMusicActivity.class);
        intent.putExtra(SheetMusicActivity.MidiDataID, data);
        intent.putExtra(SheetMusicActivity.MidiTitleID, file.toString());
        startActivity(intent);

    }


    /** As text is entered in the filter box, filter the list of
     *  midi songs to display.
     */
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        adapter.getFilter().filter(s);
    }

    public void afterTextChanged(Editable s) {
    }

   
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }


    /** Return true if the data starts with the header MTrk */
    boolean hasMidiHeader(byte[] data) {
        String s;
        try {
            s = new String(data, 0, 4, "US-ASCII");
            if (s.equals("MThd"))
                return true;
            else
                return false;
        }
        catch (UnsupportedEncodingException e) {
            return false;
        }
    }
    
    /** Show an error dialog with the given message */
    void showErrorDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int id) {
           }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}

