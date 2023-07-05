package net.javaguides.springboot.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.UniqueElements;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userId;

    @NotBlank(message = "Please provide first name")
    private String firstName;

    @NotBlank(message = "Please provide last name")
    private String lastName;

    @NotBlank(message = "Please provide email")
    @Email
    @Column(unique = true)
    private String email;

    @NotBlank(message = "Please provide password")
    @Size(min = 6)
    private String password;

    //	@ManyToMany(
//			fetch = FetchType.LAZY,
//			cascade = {
//					CascadeType.PERSIST,
//					CascadeType.MERGE
//			},
//			mappedBy = "users"
//	)
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "users_projects",
            joinColumns = @JoinColumn(
                    name = "user_id", referencedColumnName = "userId"),
            inverseJoinColumns = @JoinColumn(
                    name = "project_id", referencedColumnName = "projectId"))
//    @ManyToMany(fetch = FetchType.LAZY,
//            cascade = {
//                    CascadeType.PERSIST,
//                    CascadeType.MERGE
//            })
//    @JoinTable(name = "user_project",
//            joinColumns = { @JoinColumn(name = "project_id") },
//            inverseJoinColumns = { @JoinColumn(name = "user_id") })
    private Collection<Project> projects;

    public User(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }
}
