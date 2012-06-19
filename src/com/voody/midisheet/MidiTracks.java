/*
 * Copyright (c) 2007-2011 Madhav Vaidyanathan
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

import java.util.*;



/** @class MidiTrack
 * The MidiTrack takes as input the raw MidiEvents for the track, and gets:
 * - The list of midi notes in the track.
 * - The first instrument used in the track.
 *
 * For each NoteOn event in the midi file, a new MidiNote is created
 * and added to the track, using the AddNote() method.
 * 
 * The NoteOff() method is called when a NoteOff event is encountered,
 * in order to update the duration of the MidiNote.
 */ 
public class MidiTracks {
    private int tracknum;                 /** The track number */
    private ArrayList<MidiNotes> notes;    /** List of Midi notes */
    private int instrument;               /** Instrument for this track */
    private ArrayList<MidiEvents> lyrics;  /** The lyrics in this track */

    /** Create an empty MidiTrack.  Used by the Clone method */
    public MidiTracks(int tracknum) {
        this.tracknum = tracknum;
        notes = new ArrayList<MidiNotes>(20);
        instrument = 0;
    } 

    /** Create a MidiTrack based on the Midi events.  Extract the NoteOn/NoteOff
     *  events to gather the list of MidiNotes.
     */
    public MidiTracks(ArrayList<MidiEvents> events, int tracknum) {
        this.tracknum = tracknum;
        notes = new ArrayList<MidiNotes>(events.size());
        instrument = 0;
 
        for (MidiEvents mevent : events) {
            if (mevent.EventFlag == MidiFiles.EventNoteOn && mevent.Velocity > 0) {
                MidiNotes note = new MidiNotes(mevent.StartTime, mevent.Channel, mevent.Notenumber, 0);
                AddNote(note);
            }
            else if (mevent.EventFlag == MidiFiles.EventNoteOn && mevent.Velocity == 0) {
                NoteOff(mevent.Channel, mevent.Notenumber, mevent.StartTime);
            }
            else if (mevent.EventFlag == MidiFiles.EventNoteOff) {
                NoteOff(mevent.Channel, mevent.Notenumber, mevent.StartTime);
            }
            else if (mevent.EventFlag == MidiFiles.EventProgramChange) {
                instrument = mevent.Instrument;
            }
            else if (mevent.Metaevent == MidiFiles.MetaEventLyric) {
                if (lyrics == null) {
                    lyrics = new ArrayList<MidiEvents>();
                }
                lyrics.add(mevent);
            }
        }
        if (notes.size() > 0 && notes.get(0).getChannel() == 9)  {
            instrument = 128;  /* Percussion */
        }
    }

    public int trackNumber() { return tracknum; }

    public ArrayList<MidiNotes> getNotes() { return notes; }

    public int getInstrument() { return instrument; }
    public void setInstrument(int value) { instrument = value; }

    public ArrayList<MidiEvents> getLyrics() { return lyrics; }
    public void setLyrics(ArrayList<MidiEvents> value) { lyrics = value; }


    public String getInstrumentName() { if (instrument >= 0 && instrument <= 128)
                  return MidiFiles.Instruments[instrument];
              else
                  return "";
            }

    /** Add a MidiNote to this track.  This is called for each NoteOn event */
    public void AddNote(MidiNotes m) {
        notes.add(m);
    }

    /** A NoteOff event occured.  Find the MidiNote of the corresponding
     * NoteOn event, and update the duration of the MidiNote.
     */
    public void NoteOff(int channel, int notenumber, int endtime) {
        for (int i = notes.size()-1; i >= 0; i--) {
            MidiNotes note = notes.get(i);
            if (note.getChannel() == channel && note.getNumber() == notenumber &&
                note.getDuration() == 0) {
                note.NoteOff(endtime);
                return;
            }
        }
    }

    /** Return a deep copy clone of this MidiTrack. */
    public MidiTracks Clone() {
        MidiTracks track = new MidiTracks(trackNumber());
        track.instrument = instrument;
        for (MidiNotes note : notes) {
            track.notes.add( note.Clone() );
        }
        if (lyrics != null) {
            track.lyrics = new ArrayList<MidiEvents>();
            for (MidiEvents ev : lyrics) {
                track.lyrics.add(ev);
            }
        }
        return track;
    }

    @Override
    public String toString() {
        String result = "Track number=" + tracknum + " instrument=" + instrument + "\n";
        for (MidiNotes n : notes) {
           result = result + n + "\n";
        }
        result += "End Track\n";
        return result;
    }
}


