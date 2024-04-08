package pl.dolien.blogosphere.validation;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import pl.dolien.blogosphere.entity.Review;
import pl.dolien.blogosphere.entity.User;

import java.sql.Timestamp;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class WebPost {

    private int id;

    @NotNull(message = "is required")
    @Size(min = 1, message = "is required")
    private String title;

    @NotNull(message = "is required")
    @Size(min = 1, message = "is required")
    private String description;

    @NotNull(message = "is required")
    @Size(min = 10, message = "must have minimum 10 characters")
    private String note;

    private String privacy;

    private Timestamp date;

    private User user;

    private List<Review> reviews;

}
