package pl.dolien.freetube.validation;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class WebUser {

    @NotNull(message = "is required")
    @Size(min = 4, message = "Minimum 4 characters")
    private String userName;

    @NotNull(message = "is required")
    @Size(min = 4, message = "Minimum 4 characters")
    private String password;

    @NotNull(message = "is required")
    @Size(min = 1, message = "is required")
    private String firstName;

    @NotNull(message = "is required")
    @Size(min = 1, message = "is required")
    private String lastName;

    @NotNull(message = "is required")
    @Size(min = 5, message = "is required")
    @Pattern(regexp = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")
    private String email;

    private String emailError;

    public boolean isEmailValid() {
        if (email == null || email.isEmpty()) {
            return true;
        }

        if (!email.matches("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")) {
            emailError = "Invalid email format";
            return false;
        }

        return true;
    }

}
