package assignment4;
/*
Julia Romero
JLR5576
Project4
 */
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.time.Instant;


/**
 * Filter consists of methods that filter a list of tweets for those matching a
 * condition.
 */
public class Filter {

    /**
     * Find tweets written by a particular user.
     *
     * @param tweets
     *            a list of tweets with distinct ids, not modified by this method.
     * @param username
     *            Twitter username, required to be a valid Twitter username as
     *            defined by Tweet.getAuthor()'s spec.
     * @return all and only the tweets in the list whose author is username,
     *         in the same order as in the input list.
     */
    public static List<Tweets> writtenBy(List<Tweets> tweets, String username) {
        return tweets.stream().filter(tweet -> username.equals(tweet.getName())).collect(Collectors.toList());
    }

    /**
     * Find tweets that were sent during a particular timespan.
     *
     * @param tweets
     *            a list of tweets with distinct ids, not modified by this method.
     * @param timespan
     *            timespan
     * @return all and only the tweets in the list that were sent during the timespan,
     *         in the same order as in the input list.
     */
    public static List<Tweets> inTimespan(List<Tweets> tweets, Timespan timespan) throws InvalidTimespanException {
        if (timespan.getStart().isAfter(timespan.getEnd())) {
            throw new InvalidTimespanException("End Date before Start Date");
        }
        return tweets.stream().filter(tweet -> {
            Instant tweetTime = Instant.parse(tweet.getDate());
            return timespan.getStart().isBefore(tweetTime) && timespan.getEnd().isAfter(tweetTime);
        }).collect(Collectors.toList());
    }

    /**
     * Find tweets that contain certain words.
     *
     * @param tweets
     *            a list of tweets with distinct ids, not modified by this method.
     * @param words
     *            a list of words to search for in the tweets.
     *            A word is a nonempty sequence of nonspace characters.
     * @return all and only the tweets in the list such that the tweet text (when
     *         represented as a sequence of nonempty words bounded by space characters
     *         and the ends of the string) includes *at least one* of the words
     *         found in the words list. Word comparison is not case-sensitive,
     *         so "Obama" is the same as "obama".  The returned tweets are in the
     *         same order as in the input list.
     */
    public static List<Tweets> containing(List<Tweets> tweets, List<String> words) {
        List<String> lowerWords = words.stream().map(String::toLowerCase).collect(Collectors.toList());
        return tweets.stream().filter(tweet -> {
            String cleanedText = tweet.getText().toLowerCase().replaceAll("^[a-zA-Z]", "");
            for (String w : lowerWords) {
                if (cleanedText.contains(w)) {
                    return true;
                }
            }
            return false;
        }).collect(Collectors.toList());
    }
}
