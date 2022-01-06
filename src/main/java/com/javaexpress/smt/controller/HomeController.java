package com.javaexpress.smt.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.javaexpress.smt.dao.UserRepository;
import com.javaexpress.smt.entity.User;
import com.javaexpress.smt.helper.Message;

@Controller
public class HomeController {
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository userRepo;
	
	@GetMapping("/")
	public String home(Model model) {
		model.addAttribute("title","Home - Smart Contact Manager");
		return "home";
	}
	
	@GetMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("title","Home - Smart Contact Manager");
		model.addAttribute("user",new User());
		return "signup";
	}
	
	@PostMapping("/register")
	public String register(@Valid @ModelAttribute("user") User user,BindingResult result, Model model, HttpSession session) {
		
		if(result.hasErrors()) {
			
			model.addAttribute("user",user);

			return "signup";
		}else {
			
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			User data = userRepo.save(user);
			data.setAgreement(true);
			model.addAttribute("user",data);
			session.setAttribute("message", new Message("alert-success","User registered successfully !!!"));
			return "signup";
		}		
	}
	
	@GetMapping("/signin")
	public String signin(Model model) {
		model.addAttribute("title","Home - Smart Contact Manager");
		
		return "signin";
	}
	
	
}
