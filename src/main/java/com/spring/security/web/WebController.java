package com.spring.security.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class WebController {
	@GetMapping("courses")
	public String getHomeView() {
		return "courses";
	}

	@GetMapping("login")
	public String getLoginView() {
		return "login";
	}
}
