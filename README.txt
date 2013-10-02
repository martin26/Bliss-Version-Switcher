Name   : Bliss Version Switcher 1.0.7
Author : Jonathan Vasquez (fearedbliss)
License: MPL 2.0
Date   : Wednesday, October 2, 2013
------------------------------------------------

Description:
---------------
This is a simple Java based application that will help you manage/launch multiple versions of Diablo II from a single Window.

This means you can easily install and play every single version of Diablo II from 1.00 to the latest 1.13d (and any other future versions)
while maximizing your disk space (Since you won't have to keep having multiple copies of your MPQs), and having complete character isolation.

Requirements:
---------------
- Java 7

Structure:
---------------
The directory structure that BVS uses is as follows:

Diablo II/				- (Your Diablo II directory)
Diablo II/Classic			- All your classic versions will be in here
Diablo II/Classic/<version>		- Location of your Diablo II files for this version.
Diablo II/Classic/<version>/data	- Data folder (If you have one)
Diablo II/Classic/<version>/save	- Location of your save file for this specific version of classic

The same applies for expansion:

Diablo II/Expansion
Diablo II/Expansion/<version>
Diablo II/Expansion/<version>/data
Diablo II/Expansion/<version>/save

Instructions:
---------------
1) Install Diablo II in <PATH> (Your choice)
-) If you downloaded one of the bundles, put the 'Classic' and/or 'Expansion' folder that comes in the bundle into your D2's root directory.
2) Open up BVS and click "Add". Fill in the details. 
	> Version will be what is _currently_ in the Diablo II directory (If freshly installed, it's either 1.00 for Classic, 1.07 for Expansion, or 1.12a Expansion for Blizzard's installer)
	> Path will be the path to the Game.exe or Diablo II.exe
	> Flags will be the options that you want to pass to Diablo II
3) Once you have added the entry, and you know that you have your "Play Disc" or "Expansion Disc" in your computer, Run the game once by pressing "Launch".
   
The first time you launch the application, the application will detect the version and type (classic|expansion) and it will create the directories
needed inside your Diablo II directory. It will also backup the files for this version (If not already backed up) so that you can switch back to them, and it will also
edit the registry and modify the location of your save/ characters and also the default resolution to use (640x480 for Classic, 800x600 for Expansion).

Upgrading to a new Patch:
---------------
1. Run the patch of your choice.
2. Once the patch is complete, add another entry to BVS with the version, path, flags of your choice.
3. Click Launch. BVS will then once again backup the files, and set up the required stuff for you to play.

When you are ready to switch back to another version, just click the version you want from the entry and click Launch.

NOTE: It is important and critical for you to click Launch immediately after you Patch your directory. If you try to run another version,
then the launcher will just remove your patch and restore back to the one you selected. Clicking "Launch" immediately after you Patch will serve
as your backup.

Multiple Versions:
---------------
If you want to run multiple versions of Diablo II, It is the same process as before. Downloaded the Modified D2gfx.dll from somewhere, put it in the folder
of the Diablo II directory you want (Example: D:/Games/Diablo II/Expansion/1.09d/D2gfx.dll), and then run 1.09d from BVS. BVS will only let you run multiple
versions of the same version you are currently running. Meaning that you cannot open Expansion 1.09d and then try to open Expansion 1.10 or Classic 1.09d, etc.

Using '-direct -txt'
---------------

If you want to use a 'data' folder, You should launch the Diablo II version you want to use the 'data' folder with once.
Once you launch Diablo II you can exit it. (This updates all application pointers)

After that, add the 'data' folder you want to your Diablo II directory, and now play as normal. 

Each time you exit Diablo II, it will automatically backup the 'data' folder to your target folder.

When you switch Diablo II versions, the application will delete the 'data' folder that's in your D2's root directory, 
and it will copy the 'data' folder for the new version you want to play.

If you want to delete your data folder, close D2 if you are still playing it, delete the data folder from the D2 root directory,
and now run the same version again. The application will then detect that you are running the same version as before, but that there is
no 'data folder' in the root directory, meanwhile there is one in the backup. It will go to the backup and delete the 'data' folder there.