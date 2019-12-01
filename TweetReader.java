package assignment4;

/*
Julia Romero
JLR5576
Project4
 */
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * TweetReader contains method used to return tweets from method
 */
public class TweetReader {

    private final static ObjectMapper mapper = new ObjectMapper();

    private static final String ID = "Id";
    private static final String NAME = "Name";
    private static final String TEXT = "Text";
    private static final String DATE = "Date";

    /**
     * Find tweets written by a particular user.
     *
     * @param url
     *            url used to query a GET Request from the server
     * @return return list of tweets from the server
     *
     */
    public static List<Tweets> readTweetsFromWeb(String url) throws Exception
    {
        List<Tweets> tweetList = new ArrayList<>();
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        List<Map<String, String>> tweetObjs;
        try (Response response = client.newCall(request).execute()){
            String reqTweets = response.body().string();
//            System.out.println(reqTweets);
//            tweetList = mapper.readValue();
//            tweetList = Arrays.asList(mapper.readValue(reqTweets, Tweets[].class));
            List<Map<String, String>> data = mapper.readValue(reqTweets, new TypeReference<List<Map<String, String>>>(){});
            data.forEach(tweetObj -> {
                Tweets tweet = buildTweet(tweetObj);
                if (isTweetValid(tweet)) {
                    tweetList.add(tweet);
                }
            });
        }

        return tweetList;
    }

    private static boolean isTweetValid(Tweets tweet) {
        if (tweet.getName() == null || tweet.getDate() == null || tweet.getText() == null) {
            return false;
        }

        boolean isIdValid = tweet.getId() > 0 && tweet.getId() <= Math.pow(2, 32);
        boolean isNameValid = tweet.getName().matches("[a-zA-Z0-9_]+");
        boolean isDateValid;
        try {
            Instant.parse(tweet.getDate());
            isDateValid = true;
        } catch (DateTimeParseException e) {
            isDateValid = false;
        }
        boolean isTextValid = tweet.getText().length() <= 140;

        return isIdValid && isNameValid && isDateValid && isTextValid;
    }

    private static Tweets buildTweet(Map<String, String> tweetObj) {
        Tweets tweet = new Tweets();
        tweet.setId(Integer.parseInt(tweetObj.get(ID)));
        tweet.setName(tweetObj.get(NAME));
        tweet.setDate(tweetObj.get(DATE));
        tweet.setText(tweetObj.get(TEXT));
        return tweet;
    }

}

