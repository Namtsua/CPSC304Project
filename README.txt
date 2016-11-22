Spencer Austman -- My Suburban Library (MySQL)

This document will serve as a guide for anyone confused by the layout of this project.

In this directory, the /.settings/, /bin/, /css/, /fonts/, /js/, /lib/ directories contain the various libraries and frameworks that were used in this project (Bootstrap, JXBrowser, Parsley, etc.). I apologize for the amount of files, I could not find the time to remove all of the uneeded ones.

In the /resources/ directory, you will find three images containing the logo of our application (these were created by Brendon).

Finally, the /src/ directory contains all of my (Spencer) written code for this application and this includes both front and back end.

Going into the /src/ directory, there are two choices: the /GUI/ directory and the jdbcDB2Sample.java file.

The first one (the GUI directory) contains all of the HTML files used in this application. Each of the files contains all of the HTML/bootstrap code for a specific view (Admin page, Item page, Result page, etc.).

The second option (the java file) contains all of the Java code used in this application. The name of the file might seem odd, but it was the same file name we submitted for the second deliverable. Furthermore, I never remembered to change the name and, at this point, I'd rather not for fear of everything breaking.

It's worth noting that, if I had slightly more time, I would have done the responsible thing and separated the single .java file into multiple smaller files. I'm sorry for any annoyance this may cause.

As for the content's of the Java file, I'd like to mention that the MainView class may seem a bit odd as it loads the item.html file, this is intentional. There used to be home/main page at one point in the project, but it was scrapped in favor of starting
on a search page. Unfortunately, there were some specific methods tied to it (namely login) and, for the sake of not potentially creating multiple bugs, it simply acts as a pseudo-item search page.

Finally, I'd like to make a quick note about the comments in the Java file. As the majority, if not all, of the classes are rather similar I have only added comments two the first of each class type (either a view class or a results class). With that being said, if a class did something that no other class did, I added some comments explaining the situation.