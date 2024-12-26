package com.ot.entities;

import java.util.Date;

public class Tweet {
	private int tweetId;
	private String content;
	private Date timestamp;
	private User author;
	private boolean isPublished;

	public Tweet(int tweetId, String content, User author) {
		this.tweetId = tweetId;
		this.content = content;
		this.author = author;
		this.timestamp = new Date();
		this.setPublished(false);
	}

	public String getContent() {
		return content;
	}

	public User getAuthor() {
		return author;
	}

	public Date getTimeStamp() {
		return timestamp;
	}

	@Override
	public String toString() {
		return "Tweet (" + tweetId + ")by " + author.getUsername() + " with content" + content + " on " + timestamp;
	}

	public boolean isPublished() {
		return isPublished;
	}

	public void setPublished(boolean isPublished) {
		this.isPublished = isPublished;
	}
}
