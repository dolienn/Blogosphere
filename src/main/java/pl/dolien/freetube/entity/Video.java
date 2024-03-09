package pl.dolien.freetube.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "video")
@NoArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "title")
    @NonNull
    private String title;

    @Column(name = "url")
    @NonNull
    private String url;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE,
                          CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "user_id")
    private User user;

}
