package net.javaguides.springboot.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import net.javaguides.springboot.model.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import net.javaguides.springboot.model.User;
import net.javaguides.springboot.repository.UserRepository;
import net.javaguides.springboot.web.dto.UserRegistrationDto;

@Service
public class UserServiceImpl implements UserService{

	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	public UserServiceImpl(UserRepository userRepository) {
		super();
		this.userRepository = userRepository;
	}

	@Override
	public User save(UserRegistrationDto registrationDto) {
		User existingUser = userRepository.findByEmail(registrationDto.getEmail());
		if (existingUser != null) {
			return null;
//			throw new RuntimeException("User already exists!");
		}
		User user = new User(registrationDto.getFirstName(), 
				registrationDto.getLastName(), registrationDto.getEmail(),
				passwordEncoder.encode(registrationDto.getPassword()));
//				Arrays.asList(new Project()));
		System.out.println("UserServiceImpl.save(): user = " + user);
		return userRepository.save(user);
	}

	@Override
	public List<User> fetchAllUsersById(List<Long> selectedUsers) {
		return userRepository.findAllById(selectedUsers);
	}

	@Override
	public void deleteProjectById(Long id) {
		Collection<User> users = userRepository.findAll();
		for (User user : users) {
			user.getProjects().removeIf(project -> project.getId().equals(id));
			userRepository.save(user);
		}
	}


	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	
		User user = userRepository.findByEmail(username);
		if(user == null) {
			throw new UsernameNotFoundException("Invalid username or password.");
		}
		return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), mapRolesToAuthorities(user.getProjects()));
	}
	
	private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Project> projects){
		return projects.stream().map(project -> new SimpleGrantedAuthority(project.getName())).collect(Collectors.toList());
	}

	@Override
	public User saveUser(User user) {
		return userRepository.save(user);
	}

	@Override
	public List<User> fetchUserList() {
		return userRepository.findAll();
	}

	@Override
	public User fetchUserById(Long userId)  {
		return userRepository.findById(userId).get();
	}

	@Override
	public User fetchUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	public User fetchUserByFirstName(String firstName) {
		return userRepository.findByFirstNameIgnoreCase(firstName);
	}

	@Override
	public User fetchUserByLastName(String lastName) {
		return userRepository.findByLastNameIgnoreCase(lastName);
	}

	@Override
	public User fetchUserByFirstNameAndLastName(String firstName, String lastName) {
		return userRepository.findByFirstNameAndLastNameIgnoreCase(firstName, lastName);
	}

	@Override
	public User fetchUserByFirstNameIgnoreCase(String firstName) {
		return null;
	}

	@Override
	public User login(String email, String password) {
		return userRepository.findByEmailAndPassword(email, password);
	}
}
