package com.otassess.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.Future;

import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.SpringApplication;

import com.ot.entities.Tweet;
import com.ot.entities.User;

public class TestOtAssessmentApplication {

	public static void main(String[] args) {
		SpringApplication.from(OtAssessmentApplication::main).with(OtAssessmentApplicationTests.class).run(args);
	
	}

}
