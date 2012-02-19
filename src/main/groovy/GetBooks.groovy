@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.5.0-RC2')
import groovyx.net.http.*
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*
import java.awt.event.ItemEvent;
import java.text.SimpleDateFormat;

import groovy.util.slurpersupport.GPathResult;
import groovy.xml.StreamingMarkupBuilder;

String queryBooksUrlTemplate = "https://nlsbard.loc.gov/cgi-bin/public/nlsbardprod/querybarddatabase.cgi?DaysBack=%s"

if (args.length < 3 || args.length > 4) {
	System.err.println("Script expects 3 or 4 arguments but ${args.length} were supplied\n\t${usage()}");
	System.exit(-1);
}

String daysBack = args[0]
String username = args[1]
String password = args[2]
String dateFilter = null
if (args.length > 3 && args[3] != null && args[3].trim().length() > 0) {
	SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy")
	df.setTimeZone(TimeZone.getTimeZone("US/Eastern"))
	Date dt = df.parse(args[3])
	String full = dt.getTime().toString()
	dateFilter = full.substring(0, full.length() - 3)
	println "Filter to only books with date ${dateFilter}"
}

println "Requesting list of books for past ${daysBack} days..."
def bookNums = fetchBooks(queryBooksUrlTemplate, daysBack, dateFilter)
println "Found ${bookNums.size()} matching criteria."

def http = new HTTPBuilder( "https://nlsbard.loc.gov/cgi-bin/nlsbardprod/" )

login(http, username, password)

def bookCount = bookNums.size()
bookNums.eachWithIndex  { bookNum, i ->
	println "Downloading book ${i + 1} of ${bookCount} with number ${bookNum}..."
	download(http, bookNum.toString())
}

println "Finished downloading all books."

/**
 * Fetch the books for the last N days and optionally filter on date.
 * 
 * @param queryBooksUrlTemplate the URL template
 * @param daysBack number of days back
 * @param dateFilter number representing a date instant to use as a filter, or null for no filter
 * @return the matching books
 */
def fetchBooks(queryBooksUrlTemplate, daysBack, dateFilter) {
	def queryBooksUrl = String.format(queryBooksUrlTemplate, daysBack)
	def xml = new XmlSlurper().parse(queryBooksUrl)
	//println "Query results: ${createString(xml)}"

	def filteredBooks = dateFilter != null ? xml.book.findAll{it.date.equals(dateFilter)} : xml.book
	def bookNums = filteredBooks.collect { book ->
		book[0].children.find {it instanceof String}.trim().reverse()
	}
}

/**
 * Login to the BARD website
 * 
 * @param http the HTTPBuilder to use
 * @param username the username to use
 * @param password the password to use
 */
def login(http, username, password) {
	def postBody = [XXloginid:username,XXpassword:password,XXsubmitted:"1",XXlogin:"Login"]
	http.post (
			path: 'downloadbook.cgi',
			body: postBody,
			requestContentType: URLENC
			) {resp, reader ->
			    assert !reader.text().contains("Login attempt has failed") : "Login failed"
				assert resp.statusLine.statusCode == 200
			}
}

/**
 * Download a book from the supplied address
 * 
 * @param http the HTTPBuilder to use
 * @param bookNum the book number
 */
def download(http, bookNum) {
	def file = new FileOutputStream("${bookNum}.zip")
	def out = new BufferedOutputStream(file)
	http.get(
			path: 'downloadbook.cgi',
			query: [book: bookNum]) { resp, reader ->
				assert resp.statusLine.statusCode == 200
				out << reader
			}
}

def usage() {
	"Usage: GetBooks.groovy daysBack username password [ date in format MM-dd-yyyy ]"
}

def String createString(GPathResult root){
	return new StreamingMarkupBuilder().bind{ out << root }
}
