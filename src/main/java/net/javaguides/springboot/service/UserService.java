package net.javaguides.springboot.service;


//import org.springframework.security.core.userdetails.User;
import net.javaguides.springboot.model.User;
import net.javaguides.springboot.web.dto.UserRegistrationDto;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.ui.Model;

import java.util.List;

public interface UserService extends UserDetailsService {

	public User saveUser(User user);

	public List<User> fetchUserList();

	public User fetchUserById(Long userId);

	public User fetchUserByEmail(String email);

	public User fetchUserByFirstName(String firstName);

	public User fetchUserByLastName(String lastName);

	public User fetchUserByFirstNameAndLastName(String firstName, String lastName);

	public User fetchUserByFirstNameIgnoreCase(String firstName);

	public User login(String email, String password);

	public User save(UserRegistrationDto registrationDto);
}