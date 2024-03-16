package pl.dolien.freetube.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "user")
@NoArgsConstructor
@Getter
@Setter
@ToString(of = {"id", "userName", "enabled", "firstName", "lastName", "email", "roles"})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "username")
    private String userName;

    @Column(name = "password")
    private String password;

    @Column(name = "enabled")
    private boolean enabled;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Collection<Role> roles;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user",
               cascade = CascadeType.ALL)
    private List<Post> posts;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private List<Review> reviews;

    public User(String userName, String password, boolean enabled) {
        this.userName = userName;
        this.password = password;
        this.enabled = enabled;
    }

    public User(String userName, String password, boolean enabled,
                Collection<Role> roles, List<Post> posts, List<Review> reviews) {
        this.userName = userName;
        this.password = password;
        this.enabled = enabled;
        this.roles = roles;
        this.posts = posts;
        this.reviews = reviews;
    }

    public User(String userName, String password, boolean enabled, String firstName, String lastName, String email, Collection<Role> roles, List<Post> posts, List<Review> reviews) {
        this.userName = userName;
        this.password = password;
        this.enabled = enabled;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.roles = roles;
        this.posts = posts;
        this.reviews = reviews;
    }

    public void add(Post post) {
        if(posts == null) {
            posts = new ArrayList<>();
        }

        posts.add(post);
        post.setUser(this);
    }

    public void add(Review review) {
        if(review == null) {
            reviews = new ArrayList<>();
        }

        reviews.add(review);

    }

    public void remove(Post post) {
        posts.remove(post);
    }

    public void remove(Review review) {
        reviews.remove(review);
    }

    public boolean isAdmin(User user) {
        for (Role role : user.getRoles()) {
            if (role.getName().equals("ROLE_ADMIN")) {
                return true;
            }
        }
        return false;
    }
}
