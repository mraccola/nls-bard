Overview
========
This project consists of one Groovy script (GetBooks.groovy) that is used to 
download books from the BARD service.

The script will download all the books going N number of days back and
optionally filter the result to only books for a certain day. The script will
then attempt a login using the supplied user name and password. The script will
then download each book in the filtered book number result.

Prerequisites
=============
* Java must be installed and on the system path.

* Groovy must be instaleld and on the system path. See 
[Groovy - Installing Groovy](http://groovy.codehaus.org/Installing+Groovy).
The script was tested with version 1.8.6 but any recent version shoudl be fine.

Running
=======
The script can be run by typing the following command in a command window
(you must be in the directory containing the script):

    groovy GetBooks.groovy daysBack username password [date in format MM-dd-yyyy]

* __daysBack__ is an integer specifying the number of days back to use in the query
* __username__ is the BARD username
* __password__ is the BARD password
* __date__ is the, optional, date to use to filter the results Example: 2-17-2012

An alternative on Unix/Linux is to use the getBooks.sh bash script. This script
will prompt for the 4 parameters and then call the GetBooks.groovy script.

Outcome
=======
The script will create one ZIP file in the current directory for each book.
The name of the file will be the book number. Example: 39772.zip.

Known Issues
============
A warning message will be printed for each book. This is expected behavior. You
can safely ignore. The warning message looks like this:

    Feb 18, 2012 10:39:20 PM groovyx.net.http.ParserRegistry getAt
    WARNING: Cannot find parser for content-type: application/x-download -- using default parser.

If the username and password are invalid or missing the script will appear to
execute properly and all the files will be created. However, the contents of
each file will be invalid. You will know this has occurred if the script
executes abnormally fast and you do not see the warning message mentioned above
at all.