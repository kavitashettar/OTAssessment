package com.ot.entities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import com.otassess.task.TwitterIDGenerator;

public class User {
	private static int userCounter = 1;
	private int userId;
	private String username;
	private Set<User> followedUsers; // User can follow multiple users
	private List<Tweet> tweets; // User can have or post many tweets but tweets exist independently

	public User(String username) {
		this.userId = userCounter++;
		this.username = username;
		this.followedUsers = new HashSet<>();
		this.tweets = new ArrayList<>();
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public List<Tweet> getTweets() {
		return tweets;
	}

	// Get the list of user's this user is following
	public Set<User> getFollowedUsers() {
		return followedUsers;
	}

	public void follow(User user) {
		if (followedUsers.contains(user)) {
			return;
		} else {
			followedUsers.add(user);
		}
	}

	public void unFollow(User user) {
		if (followedUsers.contains(user)) {
			followedUsers.remove(user);
		}
	}

	public void postTweet(Tweet tweet) {
		tweets.add(tweet);
	}

	public void getNewsFeed() {

		PriorityQueue<Tweet> feed = new PriorityQueue<Tweet>(
				(o1, o2) -> o2.getTimeStamp().compareTo(o1.getTimeStamp()));

		for (User followed : followedUsers) {
			feed.addAll(followed.getTweets());
		}

		int count = 0;
		while (!feed.isEmpty() && count < 3) {
			// feed.poll() will be the most tweet
			System.out.println(feed.poll());
			count++;
		}
	}

	public List<Tweet> getTop3Feed() {

		PriorityQueue<Tweet> feed = new PriorityQueue<Tweet>(
				(o1, o2) -> o2.getTimeStamp().compareTo(o1.getTimeStamp()));

		for (User followed : followedUsers) {
			feed.addAll(followed.getTweets());
		}

		int count = 0;

		List<Tweet> topTweets = new ArrayList<Tweet>();
		while (!feed.isEmpty() && count < 3) { // feed.poll() will be the most tweet
			System.out.println(feed.poll());
			topTweets.add(feed.poll());
			count++;
		}
		return topTweets;

	}
}
