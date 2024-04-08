package pl.dolien.blogosphere.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import pl.dolien.blogosphere.entity.User;
import pl.dolien.blogosphere.service.UserService;
import pl.dolien.blogosphere.validation.WebUser;

@Controller
@RequestMapping("/register")
public class RegistrationController {

    private final UserService userService;

    @Autowired
    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);

        dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    @GetMapping("")
    public String register(Model theModel) {
        theModel.addAttribute("webUser", new WebUser());
        return "register/registration";
    }

    @PostMapping("")
    public String processRegistrationForm(@Valid @ModelAttribute("webUser") WebUser theWebUser,
                                          BindingResult theBindingResult, HttpSession session, Model theModel) {

        if (theBindingResult.hasErrors()) {
            return "register/registration";
        }

        User existing = userService.findByUserName(theWebUser.getUserName());
        if (existing != null) {
            theModel.addAttribute("webUser", new WebUser());
            theModel.addAttribute("registrationError", "User name already exists.");

            return "register/registration";
        }

        userService.save(theWebUser);

        session.setAttribute("user", theWebUser);

        return "register/registration-confirmation";
    }
}
