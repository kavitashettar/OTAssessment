package com.otassess.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.hibernate.internal.build.AllowSysOut;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.ot.entities.Tweet;
import com.ot.entities.User;
import com.otassess.task.TwitterIDGenerator;

@SpringBootApplication
public class OtAssessmentApplication {

	public static void main(String[] args) {
		SpringApplication.run(OtAssessmentApplication.class, args);
	}

	@Bean
	CommandLineRunner commandLineRunner() {
		return args -> {
			/*
			 * System.out.println("Welcome to Twitter!! Lets Tweet ");
			 * 
			 * User alice = new User("alice"); User bob = new User("bob"); User charlie =
			 * new User("Charlie"); TwitterIDGenerator twitterIDGenerator = new
			 * TwitterIDGenerator(); alice.follow(bob); alice.follow(charlie); // long
			 * tweetId = // twitterIDGenerator.increment();
			 * 
			 * 
			 * Thread.sleep(2000); Thread.sleep(2000);
			 * charlie.postTweet(tweetId,"First Tweet by charlie !"); Thread.sleep(2000);
			 * charlie.postTweet(tweetId,"Second Tweet by charlie !"); Thread.sleep(2000);
			 * System.out.println(alice.getFollowedUsers());
			 * 
			 * 
			 * alice.getNewsFeed(); List<Future> futures = new ArrayList<Future>();
			 * ExecutorService executorService = Executors.newFixedThreadPool(5);
			 * 
			 * for (Tweet tweet : alice.getTop3Feed()) { TweetTask tweetTask = new
			 * TweetTask(tweet); //Future<Integer> result =
			 * executorService.submit(tweetTask); //futures.add(result);
			 * 
			 * } System.out.println(futures.size()); executorService.shutdown();
			 * 
			 * for (int i = 0; i < 3; i++) { System.out.println( "Ranking for Tweet" +
			 * alice.getTop3Feed().get(i).toString() + " is " + futures.get(i).get()); }
			 * 
			 * alice.unFollow(charlie); System.out.println(alice.getFollowedUsers());
			 * alice.getNewsFeed();
			 */
		};
	}

}
