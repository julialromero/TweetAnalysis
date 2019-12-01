package assignment4;
/*
Julia Romero
JLR5576
Project4
 */
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Main {
    final static String URLEndpoint = "http://kevinstwitterclient2.azurewebsites.net/api/products";

 
    public static void main(String[] args) throws Exception {

        // Returning Tweets from Server
        TweetReader reader = new TweetReader();
        List<Tweets> tweetsList = reader.readTweetsFromWeb(URLEndpoint);
 
        // Filter Tweets by Username
        Filter filter = new Filter();
        List<Tweets> filteredUser = filter.writtenBy(tweetsList,"kevinyee");

        // Filter by Timespan
        Instant testStart = Instant.parse("2017-11-11T00:00:00Z");
        Instant testEnd = Instant.parse("2017-11-12T12:00:00Z");
        Timespan timespan = new Timespan(testStart,testEnd);
        List<Tweets> filteredTimeSpan = filter.inTimespan(tweetsList,timespan);

        //Filter by words containing
        List<Tweets> filteredWords = filter.containing(tweetsList,Arrays.asList("luck"));
    }
}
