package pl.dolien.freetube.validation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.dolien.freetube.entity.Post;
import pl.dolien.freetube.entity.User;

@NoArgsConstructor
@Setter
@Getter
public class WebReview {

    @NotNull(message = "is required")
    @Size(min = 1, message = "is required")
    private String comment;

    private User user;

    private Post post;
}
