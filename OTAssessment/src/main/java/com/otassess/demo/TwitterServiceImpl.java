package com.otassess.demo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.ot.entities.Tweet;
import com.ot.entities.User;
import com.otassess.task.TwitterIDGenerator;

public class TwitterServiceImpl {

	Set<User> users = new HashSet<User>();

	// store users and their tweets using Thread-safe maps
	private static final ConcurrentHashMap<User, CopyOnWriteArrayList<Tweet>> userTweets = new ConcurrentHashMap<>();

	private ReentrantReadWriteLock retweetReadWriteLock = new ReentrantReadWriteLock();
	private ReentrantReadWriteLock refollowReadWriteLock = new ReentrantReadWriteLock();

	TwitterIDGenerator twitterIDGenerator = new TwitterIDGenerator();

	ExecutorService executorService = Executors.newFixedThreadPool(5);

	// post a Tweet
	public void postATweet(User user, String content) {

		Tweet tweet = new Tweet(twitterIDGenerator.getTwitterId(), content, user);
		user.postTweet(tweet);
		//users.add(user);
		userTweets.computeIfAbsent(user, k -> new CopyOnWriteArrayList<>()).add(tweet);

	}

	public Future<List<Tweet>> getTimeline(User user) {
		return executorService.submit(() -> {
			List<Tweet> timeline = new ArrayList<Tweet>();
			retweetReadWriteLock.readLock().lock();
			refollowReadWriteLock.readLock().lock();
			try {

				// Add owns tweets
				if (userTweets.get(user) != null) {
					timeline.addAll(getTweets(user));

					// Add followees tweets
					if (!user.getFollowedUsers().isEmpty()) {

						for (User followedUser : user.getFollowedUsers()) {

							// User followedUserRes = users.get(users.indexOf(followedUser));

							timeline.addAll(getTweets(followedUser));
						}
					}
				}

			} finally {
				retweetReadWriteLock.readLock().unlock();
				refollowReadWriteLock.readLock().unlock();

			}

			return timeline;
		});
	}

	public void followUser(User follower, User followee) {
		follower.follow(followee);
		users.add(follower);

	}

	public void unfollowUser(User follower, User followee) {
		follower.unFollow(followee);
	}

	// asynchronous tweet publishing

	public Future<Tweet> publishATweetAsync(Tweet tweet) {

		return CompletableFuture.supplyAsync(() -> {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			tweet.setPublished(true);
			return tweet;

		});

	}

	// User service with asynchronous following

	public CompletableFuture<Void> followUserAsync(User follower, User followee) {

		return CompletableFuture.runAsync(() -> {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			follower.follow(followee);
		});

	}

	public List<Tweet> getTweets(User user) {
		return userTweets.getOrDefault(user, new CopyOnWriteArrayList<>());
	}
	

	public void shutdown() {
		executorService.shutdown();
		try {
			if (!executorService.awaitTermination(1, TimeUnit.SECONDS)) {
				executorService.shutdown();

			}
		} catch (InterruptedException e) {
			executorService.shutdown();
		}

	}

}
