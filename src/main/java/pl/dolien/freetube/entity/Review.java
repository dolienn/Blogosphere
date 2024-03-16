package pl.dolien.freetube.entity;

import jakarta.persistence.*;
import lombok.*;

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


}
