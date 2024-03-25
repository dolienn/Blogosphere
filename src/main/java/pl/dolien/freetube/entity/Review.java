package pl.dolien.freetube.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
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

    @Column(name = "date")
    private Timestamp date;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "post_id")
    private Post post;
}
