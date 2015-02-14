Name   : Bliss Version Switcher 1.2.1
Author : Jonathan Vasquez (fearedbliss) <jvasquez1011@gmail.com>
License: Simplified BSD License
Date   : Saturday, February 14, 2015
------------------------------------------------

Description:
---------------
This is a simple Java based application that will help you manage/launch multiple versions of Diablo II from a single window.

This means you can easily install and play every single version of Diablo II from 1.00 to the latest 1.13d (and any other future versions)
while maximizing your disk space (Since you won't have to keep having multiple copies of your MPQs), and having complete character isolation.

Requirements:
---------------
- Java 7 or Higher

Structure:
---------------
The directory structure that BVS uses is as follows:

Diablo II/                          - (Your Diablo II directory)
Diablo II/Classic                   - All your classic versions will be in here
Diablo II/Classic/<version>         - Location of your Diablo II files for this version.
Diablo II/Classic/<version>/data    - Data folder (If you have one)
Diablo II/Classic/<version>/save    - Location of your save file for this specific version of classic

The same applies for expansion:

Diablo II/Expansion
Diablo II/Expansion/<version>
Diablo II/Expansion/<version>/data
Diablo II/Expansion/<version>/save

Instructions:
---------------
1) Install Diablo II in <PATH> (Your choice)
-) If you downloaded one of the bundles, put the 'Classic' and/or 'Expansion' folder that comes in the bundle into your D2's root directory.
2) Open up BVS and click "Add". Fill in the details. (If you are using Windows Vista or higher, check the UAC instructions in the next section)
    > Version will be what is _currently_ in the Diablo II directory (If freshly installed, it's either 1.00 for Classic, 1.07 for Expansion, or 1.12a Expansion for Blizzard's installer)
    > Path will be the path to the Game.exe or Diablo II.exe
    > Flags will be the options that you want to pass to Diablo II
3) Once you have added the entry, and you know that you have your "Play Disc" or "Expansion Disc" in your computer, Run the game once by pressing "Launch".

The first time you launch the application, the application will detect the version and type (classic|expansion) and it will create the directories
needed inside your Diablo II directory. It will also backup the files for this version (If not already backed up) so that you can switch back to them, and it will also
edit the registry and modify the location of your save/ characters and also the default resolution to use (640x480 for Classic, 800x600 for Expansion).

User Account Control (for Windows Vista and Higher):
---------------
On Windows Vista, 7, 8 and higher, UAC is enabled by default and Diablo II.exe and Game.exe require administrative privileges.
Because of this, Bliss Version Switcher will not be able to launch Diablo II when you click launch (Clicking the button won't do
anything). If you are using one of these operating systems, you have two options:

1. Disable UAC.
To disable UAC, Press [Windows Key + R] and type "msconfig", then go to Tools -> Change UAC Settings, and switch the slider all the way down.
Then restart your computer. Now you can launch BlissVersionSwitcher.jar as usual and you are good to go.

2. Use the BlissVersionSwitcher_UAC.bat script to get the appropriate permissions
Launch the BlissVersionSwitcher_UAC.bat script which will ask you for administrative privileges, Windows will then give
Bliss Version Switcher correct permissions. After this, BIS (and in turn Diablo II.exe and Game.exe) will have the permissions
necessary to launch.

This approach requires your system to know where your "javaw.exe" file is which is the file that Java uses to run its applications.
If you run the script above and it works, then you don't need to do anything, however if you get an error saying that javaw wasn't found,
your system probably doesn't have java in its PATH. To add your java directory to your path, do the following:

1. Find where you installed java, and locate the bin directory (Mine is installed at: C:\Program Files\Java\jre7\bin)
2. Go to Control Panel -> System (or press [Windows Key + Pause/Break] button)
3. On the left click "Advanced System Settings" then navigate to the "Advanced" tab and at the bottom click "Environment Variables"
4. On the bottom box where it says "System Variables", find "PATH" and click "Edit"
5. Add the bin directory where java is installed to the end of the line and press Ok. Make sure there is a semicolon ( ; ) separating
the entries. Example: C:\Python33\;C:\Program Files\Java\jre7\bin
6. Run the script again :)

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
If the last version of Diablo II you played is the one you want to play now (but with a data folder),
place the 'data' folder in your Diablo II's root directory and it will be backed up once you close
the last Diablo II process (If you are running multiple clients).

If the last version you played is different than the one you want to switch to, place the 'data' folder
in its corresponding subfolder (Example: Expansion/1.13d/data), so that once you switch into this
version, the 'data' folder will be automatically copied.

