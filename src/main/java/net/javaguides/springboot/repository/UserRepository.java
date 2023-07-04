package net.javaguides.springboot.repository;

import net.javaguides.springboot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

	public User findByEmail(String email);

	public User findByEmailIgnoreCase(String email);

	public User findByFirstName(String firstName);

	public User findByFirstNameIgnoreCase(String firstName);

	public User findByLastName(String lastName);

	public User findByLastNameIgnoreCase(String lastName);

	public User findByFirstNameAndLastNameIgnoreCase(String firstName, String lastName);

	public User findByEmailAndPassword(String email, String password);

}
