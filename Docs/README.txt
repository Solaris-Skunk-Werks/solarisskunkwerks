Solaris Skunk Werks
Version: 0.5.0
Public Beta Release 2

Overview:
-------------------------------------------------------------------------------
Solaris Skunk Werks is a tool used to design Battlemechs for use with the Classic Battletech wargame.  The program was designed from the beginning to work with the newer Tech Manual style of building mechs, and does not include the older "levels of play" as such.  In other words, SSW is a program that will support Tech Manual on up, but not older books.  Right now there are very few Advanced and Experimental pieces of equipment, but as time goes by the list will lengthen.

This program was written in Java 6 SE using the Netbeans IDE.  You will need the latest version of the Java VM to run this program.  For most operating systems, the Java VM can be found here:

http://java.sun.com

Some operating systems, such as OS X, do not use the Java VM but include their own VM, so you may have to perform an update in order to use SSW.


Using the Program:
-------------------------------------------------------------------------------
First, unzip the folder "SSW" from the archive you downloaded and place it somewhere you like (such as your desktop).  The program will not work while the file is still compressed, so this is a crucial step.

On most operating systems you can simply double-click the SSW.jar file to start it.  If that does not work, you can start the program using the follwing command from a command line:

java -jar "SSW.jar"

The program may take a moment to load.  If it does not load within 20 seconds or so, you may want to end the process using the tools available on your operating system.  All the programming was done on a modern Intel Core2 Duo and I'm not sure how it will run on older systems.


Errors and How To Report Them:
-------------------------------------------------------------------------------
You may encounter an error when running the program.  Or it may simply not load.  SSW produces a logfile in it's directory that can tell me what is going wrong with it.  If you encounter problems or the program simply does not load, attach the logfile to an email and send it to me with a description of the problem.  The logfile will normally contain no information and will be overwritten whenever the program is started anew.  It is not important unless you have problems.


Current Features:
-------------------------------------------------------------------------------
1. Export to MTF
This writes the mech to an MTF file suitable for use with Megamek.  You will need to put the file into "<Megamek root>/data/mechfiles/" to use it.  Some of the later equipment is only usable in the latest development version of Megamek.

2. Export to TXT
This writes the mech to a text file in a Technical Readout format.  Like the default HTMl output, this follows my tastes, but I have included options for equipment sorting in Tools -> Options -> Export.  You can then use the resulting text file in whatever manner you please.

3. Export to HTML
This writes the mech out to an HTML file, which you can then post to the web in whatever manner you desire.  The HTML writer is a powerful tool that uses tags to write information from the program.  It uses a template->destination format, inspecting each line in the HTML_Template.html file, modifying and writing it out to a destination file named after the mech.

The HTML exporter only uses a file named "SSW/HTML_Template.html" (options will be added later), but that file need only be a text file.  You can find information on how to use the special SSW Tags in a file called "SSW/docs/HTML_Tags.txt".  If you want to build your own HTML template, create a backup of the HTML_Template.html file before you modify or build a new one.  You can even make your own Text Export format using the HTML Tags.

4. Saving and Loading
SSW now supports it's own XML save file format.  This is not the same format used by The Drawing Board or MegaMek, so please continue to use the Export to MTF option for use with MegaMek.  This format also remembers the Solaris7.com ID of the 'Mech if it was actually uploaded to Solaris7.com at some point.  This means that if you re-post it to Solaris7.com, it will update the 'Mech that is already there instead of duplicate the posting.

5. Speaking of Solaris7.com, SSW now supports direct posting of a 'Mech to one of your armories.  This feature requires an account at www.solaris7.com to work properly, and you must have created at least one armory before it can work.  You can use any of the images posted at Solaris7.com and even images you have uploaded yourself.


Requesting New Features:
-------------------------------------------------------------------------------
Simply send me an email and I will add it to the list of features to be added.  Please note that I, as the sole software writer, will not add features that do not interest me or that I feel stray from the original intent of the program.  It will eventually be released under the BSD license, so if I'm not doing something that you really want, you will be able to add it yourself.

What NOT to ask for
The basic features of this program deal with mechs, so anything dealing with other combat units in CBT will likely be rejected.  Also, if I feel that a request will step on Rick Raisley's Heavy Metal Pro program (more than I have already done) I will not add it.  Such features as unit organization and building, force BV, integration with other programs besides Megamek, etc, will never be added.


Future Plans:
-------------------------------------------------------------------------------
What you have here is only a beta version of the actual program.  It is not finished by any stretch of the imagination and many new features are planned.  Here is a quick list of planned features that will eventually make it into the program:

Production Release 1 (planned 1.0.0)
The "finished" product will allow printing and a charts page will be added to detail the strengths and weaknesses of your mech.  However, development will not stop there.

Beyond
The planned features so far are Tactical and Strategic Operations equipment as they appear, equipment restrictable by faction (Inner Sphere only), custom weapons, expanded interoperability with Megamek, and any other requested features that I find useful and want to add.


File Manifest:
-------------------------------------------------------------------------------
The following files are including in this distribution:

.\HTML_Template.html
.\README.txt
.\SSW.jar
.\S7Images

.\docs\Changelog.txt
.\docs\FAQ.txt
.\docs\HTML_Tags.txt
.\docs\HTML_Template.html.bak
.\docs\license.txt

.\lib\AbsoluteLayout.jar
