![SSW Logo](https://user-images.githubusercontent.com/1138818/115812248-5031a580-a3ae-11eb-8df4-5ec108c36bfa.png)

## Welcome to Solaris Skunkwerks

In case you missed it, this project is **ACTIVE**

### It is our pleasure to release Solaris Skunk Werks 0.7.6! Below the link we have a brief listing of the updates to SSW.

You can download the newest version, and join our development discord through our Github page here. We welcome all who want to help make SSW even better: https://github.com/Solaris-Skunk-Werks/solarisskunkwerks#solaris-skunk-werks

#### Fixes and Features:
* Fix issue causing images to not be found for units.
* Update PPC Capacitor BV calculation to match Interstellar Operations.
* Fix exporting of BFB forces to MUL format.
* Fix SAW Preference saving and loading, NOTE: You will need to set the preferences for SAW on first load as it has its own store now.
* Fix Selected Variant and Lock Chassis so that they clear and load correctly.
* Fix Roster sheet so that long Unit or Mechwarrior names now wrap to new lines.
* New Feature - New button added to Open dialog that will convert ALL units to MTF format.
* 'Set Armor Tonnage' is now pre-filled with current armor tonnage.


## Running SSW
This program was written in Java 6 SE using the Netbeans IDE.  You will need the latest version of the Java VM to run this program.  For most operating systems, the Java VM can be found here:

http://java.sun.com 

Unzip the folder "SSW" from the archive you downloaded and place it somewhere you like (such as your desktop).  The program will not work while the file is still compressed, so this is a crucial step.

On most operating systems you can simply double-click the SSW.jar file to start it.  If that does not work, you can start the program using the following command from a command line:

java -jar "SSW.jar"

The program may take a moment to load.  If it does not load within 20 seconds or so, you may want to end the process using the tools available on your operating system.  All the programming was done on various Intel Core2 Duo machines and I'm not sure how it will run on older systems.

## Loading Fixes

Sometimes you may encounter a situation where SSW will not load.  Often it is due to incompatibility with the current Java Runtime Environment installed on your computer.  Here are some fixes that we and our users have found:
 
**OS X:**
 
Apple doesn't like to play nice with Java developers and feels that the JDK from Sun isn't as good as the one they have.  Thus, it's out of date.  This is my take, anyway, and if you're going to do things Steve Job's way you're going to need help.
 
Taharga over on the Classic Battletech Forums (http://www.classicbattletech.com/forums) has found a fix for SSW when running on OS X.  Go to Applications -> Utilities -> Java -> Java Preferences and drag Java 6.0 to the top of the application runtime settings.

If that does not work, although I have not personally tested it, I recommend that OS X users give SoyLatte a try.  You can get it here:
 
http://landonf.bikemonkey.org/static/soylatte/
 
 
We also have a user-supplied fix via email (thanks, Danny!):
I'm actually very new to mac, I've always been a PC guy until just recently so when i went to the soylatte site i was at a complete loss since i'm no programmer and didn't understand half of the terminology. I did eventually find a fix though that i thought you might want to add to your web-site as being new to mac and not too comfortable with the installation of soylatte i found this fix much more simple and quick. Plus it's an actual release from apple. So it might be easier for novices like myself.

http://vegdave.wordpress.com/2007/10/13/java-se-6-mac-os-x-binary/

at the very top is a download link for java SE6 for mac OS X 10.4, apparently it existed for a very short time until apple pulled it to allow only 10.5 users to have java6. The above page has a link where someone is hosting the file for download. The only problem is that when you try to install it the system says you have a newer version already and won't install. Down in the comments someone explains how to get past this.

Nello-

"I had the same problem as the later respondents in this thread, with a nasty message telling me that I had a later version of Java installed. But, rather than giving up, I found a workaround for people comfortable with terminal access to their Mac.

Open up a terminal, and go to: /System/Library/Frameworks/JavaVM.framework/Versions/A

Now move the Resources folder away for a minute:
sudo mv Resources Resources.SAVE

Start the installer BUT DON'T INSTALL YET. All we are trying to do is get past the stupid roadblock. Once you see that installation is going to be permitted. Move the Resources folder back to its original position:
sudo mv Resources.SAVE Resources

And now, you can complete your install. Voila!"

Once i downloaded this release and followed those instructions it worked like a charm.

 
**Linux/GNU**
 
Most of the problems cropping up with Linux have to do with Ubuntu or KUbuntu, both of which are Debian relatives, so it might also happen there.
 
The first problem has to do with Drag and Drop functionality in SSW.  This is a known problem with the Linux-based JRE.  We suggest you get the latest OpenJDK using the prefered method for your distro to fix this issue.
 
However!  Getting OpenJDK may expose you to an entirely different issue!  The basic gist is that there is additional vertical spacing in the SSW controls, which makes the program barely usable.  This is a known bug with OpenJDK.  Here are some suggested fixes to try:
 
Apparently downgrading OpenJDK to 6b09 will help with the issue, or upgrading to the latest development version will help (11 seems to be an unlucky version number with OpenJDK).
 
Installing sun-java6-fonts seemed to help some users of the other program (what I found when searching for this issue).
 
You could also write a script with the following lines:
 
#!/bin/bash
export LC_ALL=en_US.ISO-8859-15
java -jar SSW.jar
 
Place this script in the same location as the SSW.jar file and ""chmod +x" it.  The character set defined as LC_ALL will depend on your locale.  If you are in the U.S. and use the U.S. English character set, the above script should work.
 
Someone also said something about "Jaunty" fixing the issue, although I have no idea what that means.  What's up with these Ubuntu people and their aversion to version numbers?
 
If all else fails on Ubuntu, Get Slack!  It really is the best Linux/GNU distro out there!
 
http://slackware.com/

## Adding Custom Weapons

[Guide to Adding Custom Weapons by Mighty Midget](http://www.mediafire.com/view/f5a9v2z48zank5f/SSW_Custom_Weapons_Guide.docx)

Go to the Docs Folder
Open CustomEquipment.ods (using either Open Office Calc or Microsoft Excel)
Select the correct Worksheet for the type of item you want to add (equipment for equipment). Ignore the fact that it says "Mech Only" on the worksheet.

Select an empty row, and enter the data for the item you wish to add to SSW (At the top of the WeaponsMechOnly sheet is a legend for each column). 

Save the file.

Depending on which program you use, the process differs at this point.

Open Office
Make sure the correct Worksheet is selected
Click File, Save As
Change the name of the file to customweapons (for weapons) or customammunition (for ammo), or customequipment (for equipment) or customphysical (for physical weapons). **Do not use the default equipment name provided by the save prompt, unless you're actually doing equipment**
Change the "Save as type" to "Text CSV (.csv) (*.csv)"
When prompted, change the Field Delimiter to ';' (Semi-colon) and leave the Text Delimiter Field Blank. Click Okay.
Click Okay to any warning messages.
Microsoft Excel
Curse
Click Start, then Control Panel, then Regional and Language Options
For Vista, click Formats Tab, and then click "Customize this Format"
For XP, click Regional Options Tab, and then click "Customize"
Change the List Separator from ',' to ';' (That's a semi-colon)
Click okay.
Go back to Excel, make sure the correct sheet is selected (so if you want to add Weapons, make sure WeaponsMechOnly is the sheet you're on).
Click file (or the Office Button, depending on version), Save As, and Other Formats
Change the name of the file to customweapons (for weapons) or customammunition (for ammo), or customequipment (for equipment) or customphysical (for physical weapons). **Do not use the default equipment name provided by the save prompt, unless you're actually doing equipment**
Change the "Save as type" to "CSV (Comma delimited) (*.csv)"
When prompted that the file type does not support workbooks with multiple sheets, click OK to save only the active sheet. 
Click Yes when prompted again about incompatible features.

(The steps from now on are the same if you're using open office or MS Office)
Close Office (either one)

You should now have a file called customweapons.csv (or customequipment.csv, or customphysicals.csv or customammunition.csv) in the Docs folder of SSW

Open the file using Notepad, or similar text editor. Delete everything up to the name of the first piece of equipment, so your first line should be a piece of equipment (or weapon). Exit notepad, saving the changes (retaining the csv file type)

Go to the main directory of SSW, double click "binconvert.jar" The source file is the .csv you just created. Click the ellipse button and navigate to the file and select it.

Using the radial buttons, select the correct file type that you're using. Click Convert.

If you've done everything right, you should get a message about writing to a new file (Such as weapons.dat). Navigate to that file, and then move the new file to the Equipment folder, overwriting the old file.

If you have different types of equipment you wish to add (such as a weapon and its ammunition), you'll need to create .csv files for each worksheet. So for the weapon you follow the instructions from Step 6 with "WeaponsMechOnly" worksheet selected, and then you'd do the same steps with the AmmunitionMechOnly Worksheet selected.

Start up SSW, it should read the changes in the equipment file, and your new item should be there. Unfortunately at this time, you'll have to redo these steps with each new release.

## Frequently Asked Questions

#### Why are older designs with Ferro Fibrous armor not "correct" in SSW?
_posted Feb 26, 2009, 6:42 PM by lostinspace@solarisskunkwerks.com_

It's a simple question of rounding.  In Tech Manual, all armor calculations are rounded down, whereas before they were rounded up.  So, many old canon designs cannot be built exactly in SSW (since it uses the newer rules).  Most people simply remove a single point of armor (always the amount of the error, so far) from the Center Torso or Center Torso (rear).

#### Where is (my favorite piece of equipment)?
_posted Feb 26, 2009, 6:00 PM by lostinspace@solarisskunkwerks.com_

First, we don't have everything in the program just yet and we do appreciate your patience with us.  We program this in our spare time.

Now, where is that piece of equipment?  Well, if you don't have the later books a good place to start is to set SSW to Experimental Tech -> All Eras (non-canon).  You can then check the availability of an item by era or even year, which makes that a great setting for lookups.  If you do have the books, check the item itself and find out when it was available.


#### When will LAMs be in the program?
_posted Feb 26, 2009, 5:57 PM by lostinspace@solarisskunkwerks.com_

I dread this question every time I hear it.  I positively hate the stupid things.  However, that is not the reason they are not included.

Bear in mind that SSW has been, and always will be, programmed from Tech Manual forward.  LAMs are old rules, and until they appear in a later book will not be included.  Fortunately, by all accounts, LAMs will be included in the upcoming Interstellar Operations book.  Once I have that in my hands I'll begin coding LAMs into the program.

# About

Solaris Skunk Werks is a tool used to design Battlemechs for use with the Battletech wargame.  The program was designed from the beginning to work with the newer Tech Manual style of building mechs, and does not include the older "levels of play" as such.  In other words, SSW is a program that will support Tech Manual on up, but not older books.

SSW is a feature-rich environment for 'Mech designers, offering a customizable HTML export, standard Technical Readout style Text Export, the ability to export your creation to a single line of text suitable for online chat (the so-called "Chat Export"), and even export to the popular and excellent MegaMek game in the form of an MTF file.  We support the latest record sheet printing and will eventually support Tactical Operations record sheets, and offer a customizable armor and internals dot pattern in the form of dot images, as well as a compact, easily marked-off format for more "utilitarian" users.  Our record sheets can also be printed with the most common charts used in the table-top Battletech game.

SSW was originally designed as a "writer's tool" for 'Mechs, and is dedicated and programmed for the users of the Solaris 7 online community.  We plan to expand our "fluff" writing support in the near future with RTF style controls that will appear on the appropriate exports as well as a spell-checker, which will hopefully include the more common Battletech terms.  We also plan to include any custom images of your 'Mech in the actual savefile.

As you can see SSW is, and probably always will be, a work in-progress.  We hope you find this work useful and look forward to any feedback you have about our creation.

Legaleze:
MechWarrior, BattleMech, ‘Mech and AeroTech are registered trademarks of The Topps Company, Inc. All rights reserved.
Catalyst Game Labs and the Catalyst Game Labs logo are trademarks of InMediaRes Productions, LLC.

Solaris Skunk Werks License:
Solaris Skunk Werks is published under the BSD License, and some parts are covered under the GNU Lesser General Public License.  A copy of both these licenses is provided in "SSW/docs".  You may freely download the latest sourcecode.
