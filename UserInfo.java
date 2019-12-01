package assignment4;
/*
Julia Romero
JLR5576
Project4
 */
import java.util.HashSet;
import java.util.Set;

public class UserInfo {
    private Set<String> followers;
    private Set<String> follows;
    private Set<String> friends;
    private String user;

    public UserInfo(String user) {
        follows = new HashSet<>();
        followers = new HashSet<>();
        friends = new HashSet<>();
        this.user = user;
    }

    public Set<String> getFollowers() {
        return followers;
    }

    public void setFollowers(Set<String> followers) {
        this.followers = followers;
    }

    public Set<String> getFollows() {
        return follows;
    }

    public void setFollows(Set<String> follows) {
        this.follows = follows;
    }

    public void setFriends(Set<String> friends){
        this.friends = friends;
    }

    public Set<String> getFriends(){
        return friends;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
