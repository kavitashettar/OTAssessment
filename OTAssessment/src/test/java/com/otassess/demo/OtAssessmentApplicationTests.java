package com.otassess.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import com.ot.entities.Tweet;
import com.ot.entities.User;

@SpringBootTest
class OtAssessmentApplicationTests {

	@Test
	void contextLoads() {
	}

	private TwitterServiceImpl twitterServiceImpl;

	@BeforeEach
	void setup() {
		twitterServiceImpl = new TwitterServiceImpl();
	}

	@AfterEach
	void tearDown() {
		twitterServiceImpl.shutdown();
	}

	@SuppressWarnings("unlikely-arg-type")
	@Test
	void testPostATweet() throws Exception {

		User alice = new User("alice");
		User bob = new User("bob");
		User charlie = new User("Charlie");
		twitterServiceImpl.postATweet(alice, "Alice's 1st Tweet");
		twitterServiceImpl.postATweet(alice, "Alice's 2nd Tweet");

		Future<List<Tweet>> timeline = twitterServiceImpl.getTimeline(alice);
		List<Tweet> result = timeline.get();
		assertEquals(2, result.size());
		for (Tweet tweet : result) {
			assertTrue(() -> (tweet.getContent().contentEquals("Alice's 1st Tweet")
					|| tweet.getContent().contentEquals("Alice's 2nd Tweet")));
		}

	}

	@Test
	void testFollowAndTimeline() throws Exception {
		User alice = new User("alice");
		User bob = new User("bob");
		User charlie = new User("Charlie");
		twitterServiceImpl.postATweet(alice, "Alice's 1st Tweet");
		twitterServiceImpl.postATweet(bob, "Bob's 1st Tweet");
		twitterServiceImpl.postATweet(charlie, "Charlie's 1st Tweet");

		twitterServiceImpl.followUser(alice, bob);
		twitterServiceImpl.followUser(alice, charlie);

		// wait for follow and tweets to process
		Thread.sleep(1000);

		Future<List<Tweet>> timeline = twitterServiceImpl.getTimeline(alice);
		List<Tweet> result = timeline.get();
		System.out.println(result);
		assertEquals(3, result.size());
		for (Tweet tweet : result) {
			assertTrue(() -> (tweet.getContent().contentEquals("Alice's 1st Tweet")
					|| tweet.getContent().contentEquals("Bob's 1st Tweet")
					|| tweet.getContent().contentEquals("Charlie's 1st Tweet")));

		}
	}

	@Test
	void testUnfollow() throws Exception {

		User alice = new User("alice");
		User bob = new User("bob");
		User charlie = new User("Charlie");
		twitterServiceImpl.postATweet(alice, "Alice's 1st Tweet");
		twitterServiceImpl.postATweet(bob, "Bob's 1st Tweet");
		twitterServiceImpl.postATweet(charlie, "Charlie's 1st Tweet");

		twitterServiceImpl.followUser(alice, bob);
		twitterServiceImpl.followUser(alice, charlie);

		Thread.sleep(1000);

		twitterServiceImpl.unfollowUser(alice, bob);
		// Wait for the unfollow
		Thread.sleep(1000);

		Future<List<Tweet>> timeline = twitterServiceImpl.getTimeline(alice);
		List<Tweet> result = timeline.get();
		System.out.println(result);
		assertEquals(2, result.size());
		for (Tweet tweet : result) {
			assertTrue(() -> (tweet.getContent().contentEquals("Alice's 1st Tweet")
					|| tweet.getContent().contentEquals("Charlie's 1st Tweet")));

		}

	}

	@Test
	void testConcurrentTweetsOrPosts() throws Exception {
		ExecutorService testExecutor = Executors.newFixedThreadPool(5);
		List<User> users = new ArrayList<User>();

		User ankit = new User("Ankit");
		users.add(ankit);
		User anshika = new User("Anshika");
		users.add(anshika);
		User anoop = new User("Anoop");
		users.add(anoop);
		User kavita = new User("Kavita");
		users.add(kavita);

		for (User user : users) {
			testExecutor.submit(() -> twitterServiceImpl.postATweet(user, "Tweet from User " + user.getUserId()));
		}

		testExecutor.shutdown();
		testExecutor.awaitTermination(1, TimeUnit.SECONDS);

		// Verify each user has one tweet
		for (User user : users) {
			Future<List<Tweet>> timeline = twitterServiceImpl.getTimeline(user);
			List<Tweet> res = timeline.get();
			assertEquals(1, res.size());
			System.out.println(res.get(0).getContent());
			assertTrue(res.get(0).getContent().contentEquals("Tweet from User " + user.getUserId()));
		}
	}

	@Test
	void testPublishTweetAsync() throws Exception {
		// Publish a tweet asynchronously
		User ankit = new User("Ankit");
		twitterServiceImpl.postATweet(ankit, "Ankit's 1st Tweet");

		Future<List<Tweet>> timeline = twitterServiceImpl.getTimeline(ankit);
		List<Tweet> result = timeline.get();
		Future<Tweet> tweetFuture = twitterServiceImpl.publishATweetAsync(result.get(0));
		assertTrue(tweetFuture.get().isPublished());
	}

	@Test
	void testfollowUserAsync() throws Exception {

		User ankit = new User("Ankit");
		User anshika = new User("Anshika");

		// Follow a user asynchronously
		CompletableFuture<Void> followFuture = twitterServiceImpl.followUserAsync(ankit, anshika);

		if (followFuture.isDone())
			assertTrue(ankit.getFollowedUsers().contains(anshika));
	}

	@Test
	void testBlockingQueueFunctionality() throws Exception {
		// create blocking queue
		Set<User> users = new HashSet<User>();
		BlockingQueue<Tweet> tweetQueue = new LinkedBlockingDeque<>(4);

		User ankit = new User("Ankit");
		users.add(ankit);

		ExecutorService testExecutor = Executors.newFixedThreadPool(5);

		for (User user : users) {
			testExecutor.submit(() -> twitterServiceImpl.postATweet(user, "Tweet from User " + user.getUserId()));
		}

		testExecutor.shutdown();
		testExecutor.awaitTermination(1, TimeUnit.SECONDS);

		Thread producer = new Thread(() -> {
			for (User user : users) {

				tweetQueue.addAll(twitterServiceImpl.getTweets(user));
			}

		});

		Thread consumer = new Thread(() -> {
			try {

				Thread.sleep(10); // Ensure producer runs first

				Tweet tweet = tweetQueue.take();
				System.out.println("Tweet :" + tweet.getContent() + "is published");
				tweet.setPublished(true);

			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}

		});

		producer.start();
		consumer.start();

		producer.join();
		consumer.join();

		assertTrue(tweetQueue.isEmpty()); // Queue should be empty

	}

}
