package pl.dolien.blogosphere.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "review")
@NoArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "comment")
    @NonNull
    private String comment;

    @Column(name = "creation_date")
    private Timestamp date;

    @Column(name = "edit_date")
    private Timestamp editDate;

    @Column(name = "edited")
    private boolean edited;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "post_id")
    private Post post;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "review",
            cascade = CascadeType.ALL)
    private List<ReviewLike> likes;

    public void addLike(ReviewLike like) {
        if (likes == null) {
            likes = new ArrayList<>();
        }
        likes.add(like);
        like.setReview(this);
    }

    public boolean hasUserLiked(User user) {
        return likes != null && likes.stream().anyMatch(like -> like.getUser().equals(user));
    }

    public int getLikesCount() {
        return likes != null ? likes.size() : 0;
    }
}
