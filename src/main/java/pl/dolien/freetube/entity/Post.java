package pl.dolien.freetube.entity;

import jakarta.persistence.*;
import lombok.*;

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

    @Column(name = "note")
    private String note;

    @Column(name = "privacy")
    private String privacy;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE,
                              CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "post_id")
    private List<Review> reviews;

    public void add(Review review) {
        if(reviews == null) {
            reviews = new ArrayList<>();
        }

        reviews.add(review);
    }

    public void remove(Review review) {
        reviews.remove(review);
    }

    public Post(String title, String note, String privacy) {
        this.title = title;
        this.note = note;
        this.privacy = privacy;
    }

    public Post(String title, String note, String privacy, List<Review> reviews) {
        this.title = title;
        this.note = note;
        this.privacy = privacy;
        this.reviews = reviews;
    }
}
