Name   : Bliss Version Switcher 1.4.3
Author : Jonathan Vasquez (fearedbliss) <jon@xyinn.org>
License: 2-BSD
Date   : March 4, 2017
------------------------------------------------

Description:
---------------
This is a simple Java based application that will help you manage/launch multiple versions of Diablo II from a single window.

This means you can easily install and play every single version of Diablo II from 1.00 to the latest 1.14d (and any other future versions)
while maximizing your disk space (Since you won't have to keep having multiple copies of your MPQs), and having complete character isolation.

Requirements:
---------------
- Java 7 or Higher
- A 1.00/1.03/or 1.07 Diablo II disc (If you want to play versions before 1.12). You _cannot_use a 1.12 disc to play these versions
  since the internal structure of the disc has changed.

Initial Setup:
---------------
We first need to place the files in the correct location and synchronize the switcher with
the version of Diablo II that is currently installed on the system. This is done because the
switcher doesn't know what version you have on your machine and so it will be "out of step".
Once we do this, the switcher will correctly switch between the versions since it keeps track
of it.

We will assume the following:
- You installed Diablo II in C:\Program Files\Diablo II\
- You downloaded "Bliss Version Switcher"
- You downloaded one of the Bundles that includes all of the Diablo II versions ready to go
- The _initial_ version of Diablo II in the folder is 1.07.
- If you want to play only LOD (Versions 1.07+): Your Diablo II "Expansion Disc" must be labeled 1.00 , 1.03, or 1.07
  and it must be inserted in computer (You cannot use the 1.12 labeled CD).
- If you want to play versions older than 1.07, you must have a 1.00 or 1.03 labeled "Play Disc" inserted in your computer.

1. Drop the "BlissVersionSwitcher.jar" file into your Diablo II folder.
2. Drop the "classic" and/or "Expansion" folder(s) that you downloaded into your Diablo II folder.
3. Open the program
4. Click "Add" and fill in the information required and press Add.
   > Select the version that is currently in the Diablo II folder (In our example, it is 1.07).
   > Type in the path to your Diablo II folder (Example: C:\Program Files\Diablo II\)
   > Type in any flags you want if needed (Example: -w -ns -direct -txt). You can leave this blank.
   > Check the "Expansion" check box if you want to play LoD (This doesn't affect the characters, only what disc (game mode) to use).
5. Start Diablo II by clicking "Launch"

After this first run, BVS and the Diablo II directory will be in Sync (Versions will be properly tracked).

Moving Existing Characters and DLLs to new locations:
---------------
If you have any existing characters, just take the characters from your specific version's save folder and put it into
its new location (Example, if you already had some 1.07 expansion characters, you can place them in: C:\Program Files\Diablo II\Expansion\1.07\save).

Path A) If you are using any custom .dlls (Like d2gfx.dll), you can place this dll in its respective folder (Example: ..\Expansion\1.07 folder *before switching to that version*).

Path B) If you already switched into that version, you can drop the dll into the Diablo II folder and also the respective version folder (or switch to a different
version and switch back into the version you want after you followed Path A.

"data" folder
---------------
BVS can run from any location on disk. However, the "-direct -txt" command seems to require that the data folder be in the Diablo II directory.
For this reason, if you are planning on using a data folder, place BVS inside the Diablo II directory as well.

Adding versions afterwards:
---------------
If you want to add any other versions, you can just click "Copy" to make a clone of an existing entry,
and quickly switch the "Version", "Flags" and "Expansion" options. The "Path" can stay the same.

You can also click "Add" and do it from scratch as well, but using the "Copy" option is just a way to save time.

Blizzard released a new patch but it isn't in the Bliss Complete Collection
----------------
Simply run the LOD updater either from the Standalone updater or from Battle.net,
close D2 (if using the latter), select the entry that was last ran and uncheck the "Last Ran"
checkbox, then add a new entry with the new version.

Running BVS after this should work flawlessly and it should automatically backup the new version
files as well.

"Oh My... I broke it"
----------------
If you screw up the install and your Diablo II directory files get messed up, just relax.. you don't need to reinstall.
Just extract the files from the Bundle for the version you want and drop them into your
Diablo II folder (Replacing the existing files in there). You already have the MPQ files, so simply replacing
the dlls should do the trick, and re-running that version in BVS should fix it.
