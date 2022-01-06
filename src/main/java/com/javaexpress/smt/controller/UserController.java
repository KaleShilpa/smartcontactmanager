package com.javaexpress.smt.controller;

import java.security.Principal;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.javaexpress.smt.dao.ContactRepository;
import com.javaexpress.smt.dao.UserRepository;
import com.javaexpress.smt.entity.Contact;
import com.javaexpress.smt.entity.User;
import com.javaexpress.smt.helper.Message;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContactRepository contactRepository;
	
	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {
		String username = principal.getName();
		User user = userRepository.getUserByUserName(username);
		model.addAttribute("user", user);
	}
	
	@GetMapping("/index")
	public String dashboard(Model model) {
		model.addAttribute("title", "User Dashboard");
		return "user/user_dashboard";
	}
	
	@GetMapping("/open-add-contact")
	public String openAddContact(Model model) {
		
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());
		return "user/add_contact";
	}
	
	@PostMapping("/save-contact")
	public String saveContact(@ModelAttribute("contact") Contact contact, Principal principal, HttpSession session) {
		
		String name = principal.getName();
		User user = this.userRepository.getUserByUserName(name);
		/*try {
			// processing and uploading file
			if (file.isEmpty()) {
				System.out.println("No images selected");
			} else {
				contact.setImageUrl(file.getOriginalFilename());
				File saveFile = new ClassPathResource("static/img").getFile();
				Path path =Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}*/		
		
		contact.setUser(user);
		
		user.getContacts().add(contact);
		this.userRepository.save(user);
		
		session.setAttribute("message", new Message("alert-success","Contact Added successfully !!!"));
		return "user/add_contact";
	}
	
	@GetMapping("/show-contacts/{page}")
	public String showContacts(Model model,@PathVariable("page") Integer page) {
		
		model.addAttribute("title", "Show Contacts");
		User user = (User)model.getAttribute("user");
		Pageable pageable = PageRequest.of(page, 5);
		Page<Contact> contacts = this.contactRepository.getContactsByUser(user.getId(),pageable);
		model.addAttribute("contacts",contacts);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages",contacts.getTotalPages());
		return "user/show_contacts";
	}
	
	@GetMapping("/contact/{cid}")
	public String contactProfile(Model model,@PathVariable("cid") Integer cid) {
		
		model.addAttribute("title", "Contact Profile");
		
		Optional<Contact> contact = this.contactRepository.findById(cid);
		User user = (User)model.getAttribute("user");
		Contact c = contact.get();
		if (user.getId() == c.getUser().getId()) {
			model.addAttribute("contact", contact.get());
		}
		
		return "user/contact_profile";
	}
	
	@GetMapping("/delete/{cid}")
	public String deleteContact(Model model,@PathVariable("cid") Integer cid, HttpSession session) {
		
		Optional<Contact> contact = this.contactRepository.findById(cid);
		User user = (User)model.getAttribute("user");
		Contact c = contact.get();
		if (user.getId() == c.getUser().getId()) {
			c.setUser(null);
			this.contactRepository.delete(c);
		}
		session.setAttribute("message", new Message("alert-success","Contact deleted successfully !!!"));
		return "redirect:/user/show-contacts/0";
	}
	@PostMapping("/update-contact")
	public String updateContact(@ModelAttribute("contact") Contact contact, Model model, HttpSession session) {
		
		User user = (User)model.getAttribute("user");
		contact.setUser(user);
		this.contactRepository.save(contact);
		session.setAttribute("message", new Message("alert-success","Contact updated successfully !!!"));
		return "redirect:/user/contact/"+contact.getCid();
	}
	
	@PostMapping("/open-update-contact/{cid}")
	public String openUpdateContact(Model model, @PathVariable("cid") Integer cid) {
		
		model.addAttribute("title", "Update Contact");
		Optional<Contact> contact = this.contactRepository.findById(cid);
		model.addAttribute("contact", contact.get());
		return "user/update_contact";
	}
	
	@GetMapping("/your-profile")
	public String yourProfile(Model model) {
		
		model.addAttribute("title", "Your Profile");
		
		return "user/your_profile";
	}
	
	@GetMapping("/settings")
	public String settings(Model model) {
		
		model.addAttribute("title", "Your Settings");
		
		return "user/settings";
	}
	
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("oldPassword") String oldPassword,
			@RequestParam("newPassword") String newPassword, Model model, HttpSession session, Principal principal) {
		
		User currentUser = (User) model.getAttribute("user");
		if(this.bCryptPasswordEncoder.matches(oldPassword, currentUser.getPassword())) {
			currentUser.setPassword(bCryptPasswordEncoder.encode(newPassword));
			currentUser.setAgreement(true);
			this.userRepository.save(currentUser);
			session.setAttribute("message", new Message("alert-success","Your Password successfully changed !!!"));
		}else {
			session.setAttribute("message", new Message("alert-error","Please enter correct old password"));
			return "redirect:/user/settings";
		}
		
		return "redirect:/user/index";
	}
	
}
