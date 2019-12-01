package assignment4;
/*
Julia Romero
JLR5576
Project4
 */
import java.util.*;
import java.util.stream.Collectors;
import java.util.Set;

/**
 * Social Network consists of methods that filter users matching a
 * condition.
 * <p>
 * DO NOT change the method signatures and specifications of these methods, but
 * you should implement their method bodies, and you may add new public or
 * private methods or classes if you like.
 */
public class SocialNetwork {
    private static Set<Integer> tweetIds = new HashSet<>();
    private static Map<String, List<String>> tweetsMap = new HashMap<>();
    private static Map<String, UserInfo> infoMap = new HashMap<>();


    /**
     * Get K most followed Users.
     *
     * @param tweets list of tweets with distinct ids, not modified by this method.
     * @param k      integer of most popular followers to return
     * @return the set of usernames who are most mentioned in the text of the tweets.
     * A username-mention is "@" followed by a Twitter username (as
     * defined by Tweet.getName()'s spec).
     * The username-mention cannot be immediately preceded or followed by any
     * character valid in a Twitter username.
     * For this reason, an email address like ethomaz@utexas.edu does NOT
     * contain a mention of the username.
     * Twitter usernames are case-insensitive, and the returned set may
     * include a username at most once.
     */
    public static List<String> findKMostFollower(List<Tweets> tweets, int k) {
        initializeNetwork(tweets);

        List<String> mostFollowers = new ArrayList<>();

        Map<Integer, Set<String>> followerCountMap = countFollowers();
        List<Integer> followCounts = new ArrayList<>(followerCountMap.keySet());
        followCounts.sort(Collections.reverseOrder());

        int index = 0;
        while (mostFollowers.size() < k) {
            if(mostFollowers.size() >= infoMap.size()){
                System.out.println(infoMap.size());
                System.out.println(mostFollowers.size());
                return mostFollowers;

            }
            Set<String> users = followerCountMap.get(followCounts.get(index));
            for (String u : users) {
                if (mostFollowers.size() < k) {
                    mostFollowers.add(u);
                }
            }
            index++;
        }

        return mostFollowers;
    }

    /**
     * Find all cliques in the social network.
     *
     * @param tweets list of tweets with distinct ids, not modified by this method.
     * @return list of set of all cliques in the graph
     */
    List<Set<String>> findCliques(List<Tweets> tweets) {
        initializeNetwork(tweets);
        List<Set<String>> cliques = new ArrayList<>();
        returnMaxCliques(new HashSet<>(), infoMap.keySet(), new HashSet<>(), cliques);

        return cliques;
    }

    // implement Bron-Kerbosch algorithm
    private void returnMaxCliques(Set<String> potentialClique, Set<String> remainingUsers, Set<String> visited, List<Set<String>> cliqueList) {
        if (remainingUsers.isEmpty() && visited.isEmpty()) {
            if (potentialClique.size() > 1)
                cliqueList.add(new HashSet<>(potentialClique));
            return;
        }

        Set<String> newRemainingUsers = new HashSet<>(remainingUsers);
        for (String s : remainingUsers) {
            Set<String> intersection1 = new HashSet<>(infoMap.get(s).getFriends());
            Set<String> intersection2 = new HashSet<>(infoMap.get(s).getFriends());
            potentialClique.add(s);
            intersection1.retainAll(newRemainingUsers);
            intersection2.retainAll(visited);

            returnMaxCliques(potentialClique, intersection1, intersection2, cliqueList);

            potentialClique.remove(s);
            newRemainingUsers.remove(s);
            visited.add(s);
        }
    }

    private static void initializeNetwork(List<Tweets> tweets) {
        Set<Integer> tweetIds = tweets.stream().map(Tweets::getId).collect(Collectors.toSet());
        if (tweetIds.equals(SocialNetwork.tweetIds)) {
            return;
        }
        SocialNetwork.tweetIds = tweetIds;

        for (Tweets tweet : tweets) {
            addTweet(tweet);
        }
        findMentions();
        findFriends();
        countFollowers();
    }


    // Add new usernames to network map, or if username is already in map adds tweet to the username
    private static void addTweet(Tweets tweet) {
        if (tweetsMap.containsKey(tweet.getName().toLowerCase())) {
            tweetsMap.get(tweet.getName().toLowerCase()).add(tweet.getText());
        } else {
            List<String> newEntry = new ArrayList<>();
            newEntry.add(tweet.getText());
            tweetsMap.put(tweet.getName().toLowerCase(), newEntry);
        }
    }

    // Build Map of User to Set of the users they mention
    private static void findMentions() {
        tweetsMap.forEach((user, tweetList) -> {
            UserInfo thisUserInfo = infoMap.get(user);
            if (thisUserInfo == null) {
                thisUserInfo = new UserInfo(user);
            }

            Set<String> handles = new HashSet<>();
            tweetList.forEach(tweetTxt -> handles.addAll(recordHandles(user, tweetTxt)));

            handles.forEach(handle -> {
                UserInfo thatUserInfo = infoMap.get(handle);
                if (thatUserInfo == null) {
                    thatUserInfo = new UserInfo(handle);
                }

                thatUserInfo.getFollowers().add(user);
            });

            infoMap.put(user, thisUserInfo);
            thisUserInfo.setFollows(handles);
        });
    }

    private static void findFriends() {
        infoMap.values().forEach(userInfo -> {
            userInfo.getFollows().forEach(follow -> {
                UserInfo followInfo = infoMap.get(follow);
                if (followInfo.getFollows().contains(userInfo.getUser())) {
                    userInfo.getFriends().add(follow);
                    followInfo.getFriends().add(userInfo.getUser());
                }
            });
        });
    }

    // Build Map of User to the # times they are mentioned
    private static Map<Integer, Set<String>> countFollowers() {
        Map<Integer, Set<String>> followerCounts = new HashMap<>();

        infoMap.values().forEach(userInfo -> {
            int followerCount = userInfo.getFollowers().size();
            if (followerCounts.containsKey(followerCount)) {
                followerCounts.get(followerCount).add(userInfo.getUser());
            } else {
                Set<String> users = new HashSet<>();
                users.add(userInfo.getUser());
                followerCounts.put(followerCount, users);
            }
        });

        return followerCounts;
    }

    // Parses a  string and returns List of the contained valid mentioned usernames
    private static List<String> recordHandles(String user, String text) {
        List<String> handles = new ArrayList<>();
        List<String> textArr = Arrays.asList(text.split("\\s+"));
        textArr.forEach(word -> {
            if (word.length() < 2) ;

            else if (word.substring(0, 2).equals("^@") || word.substring(0, 2).equals("&@")) {
                if (tweetsMap.containsKey(word.toLowerCase().substring(2))) {
                    if (!word.substring(2).toLowerCase().equals(user))
                        handles.add(word.substring(2).toLowerCase());
                }
            } else if (word.substring(0, 1).equals("@")) {
                if (tweetsMap.containsKey(word.toLowerCase().substring(1))) {
                    if (!word.substring(1).toLowerCase().equals(user))
                        handles.add(word.substring(1).toLowerCase());
                }
            }
        });
        return handles;
    }

}


