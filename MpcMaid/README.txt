MPC Maid ("MM") is an open-source (free) software editor for the Akai MPC 500, MPC 1000, and MPC 2500.
It works on Mac, PC, and other platforms (Java) and makes the edition of MPC programs files easy, with some automated features for multisamples creation, batch creation of listing programs from nested folders of samples.

With MPC Maid, get the best out of your MPC!

Basic Operations:
 Multiple window, one window for one program, simply drag and drop a .PGM program file will open a new window for this program.
 Switch between the MPC500 12-pads layout or the MPC1000 16-pads layout (and also the number of filters and sliders thanks to the built-in  machine profiles).
 Simply drag and drop your samples files onto the pads, and it automatically assigns them, one on each pad or one on each sample layer.
 Export the full program and every related sample file together to the target location of your choice.

Advanced Features:
 Slicing tool: Drag and drop your loop file (no longer than 2 bars) to have it chopped into slices based on a beat detection mechanism. Then directly export the chopped slices as multiple .WAV files, one corresponding .PGM program file and one MIDI groove .MID file.
 Multisample Creation: On drag and drop of .wav files with a consistent naming (that includes the note name, e-g "C#3", "E5" or "D 4"), you can automatically create Multisample program: MPC Maid calculates the allocation of the samples across pads and the tuning of each pad to reconstitute a complete chromatic scale.
 Batch Create Program: Automatically creates a program (.pgm) that lists every .wav sample in each directory and sub-directory, recursively from a given base directory. This allows for instant conversion of any .wav sound library into an MPC-readable library.
 Copy Settings to All Pads: You want to quickly change parameters for every pad? Set your parameters on one pad, then copy them to every other pad, except of course the sample name, tuning and midi note.


Known issues:
 In the chop slices tab, the focus is not guaranted to be on the waveform zone, when it is not you need to click in this zone to make sure the keyboard shortcuts work.
 On Vista the sound playback can be unreactive when multiple pads are pressed very quickly.
 On Mac OS X 10.4, in the chop slices tab, sound playback can in some rare case block the application, forcing to restart.



-------------------------------------------------------------------------------

MPC Maid ("MM")
Copyright 2009 Cyrille Martraire
cyrille.martraire.com

Project homepage:
http://mpcmaid.sourceforge.net/
-------------------------------------------------------------------------------
Reference

Inspired by the excellent MPC Pad 187 by Stephen Norum, that is unfortunately not portable on other OS'es than Mac OS X.
http://mybunnyhug.com

-------------------------------------------------------------------------------
MPC1000 File Format

Based on the extensive documentation provided by Stephen Norum:
http://mybunnyhug.com/fileformats/pgm/

-------------------------------------------------------------------------------
Automatic Slicing

Beat detection follows the excellent guide "Beat Detection Algorithms" from Frédéric Patin: 
http://www.gamedev.net/reference/programming/features/beatdetection/

-------------------------------------------------------------------------------
Portable browser launcher (help):

http://www.centerkey.com/java/browser/

Bare Bones Browser Launch
Version 1.5 (December 10, 2005)
By Dem Pilafian
Public Domain Software -- Free to Use as You Like
-------------------------------------------------------------------------------