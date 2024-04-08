package pl.dolien.blogosphere.validation;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import pl.dolien.blogosphere.entity.Post;
import pl.dolien.blogosphere.entity.User;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class WebReview {

    private int id;

    @NotNull(message = "is required")
    @Size(min = 1, message = "is required")
    private String comment;

    private User user;

    private Post post;

    private int postId;
}
