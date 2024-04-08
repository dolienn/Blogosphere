package pl.dolien.blogosphere.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "post")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "note")
    private String note;

    @Column(name = "privacy")
    private String privacy;

    @Column(name = "creation_date")
    private Timestamp date;

    @Column(name = "edit_date")
    private Timestamp edited;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE,
                              CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "post",
            cascade = CascadeType.ALL)
    private List<Review> reviews;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "post",
               cascade = CascadeType.ALL)
    private List<PostLike> likes;

    public void add(Review review) {
        if(reviews == null) {
            reviews = new ArrayList<>();
        }

        reviews.add(review);
    }

    public void remove(Review review) {
        reviews.remove(review);
    }

    public void addLike(PostLike like) {
        if (likes == null) {
            likes = new ArrayList<>();
        }
        likes.add(like);
        like.setPost(this);
    }

    public boolean hasUserLiked(User user) {
        return likes != null && likes.stream().anyMatch(like -> like.getUser().equals(user));
    }

    public int getLikesCount() {
        return likes != null ? likes.size() : 0;
    }

    public Post(String title, String description, String note, String privacy) {
        this.title = title;
        this.description = description;
        this.note = note;
        this.privacy = privacy;
    }

    public Post(String title, String description, String note, String privacy, List<Review> reviews) {
        this.title = title;
        this.description = description;
        this.note = note;
        this.privacy = privacy;
        this.reviews = reviews;
    }
}
