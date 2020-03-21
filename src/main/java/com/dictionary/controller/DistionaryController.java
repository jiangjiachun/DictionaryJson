package com.dictionary.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DistionaryController {

	@GetMapping("/")
	public String index() {
		return "index.html";
	}
}
