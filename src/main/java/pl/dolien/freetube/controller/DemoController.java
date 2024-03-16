package pl.dolien.freetube.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.dolien.freetube.entity.Post;
import pl.dolien.freetube.entity.User;
import pl.dolien.freetube.service.PostService;
import pl.dolien.freetube.service.UserService;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Controller
public class DemoController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String showLanding(Model m) {
        Iterable<Post> posts = postService.findAll();
        m.addAttribute("posts", posts);

        return "landing";
    }

    @PostMapping("/")
    public String search(@RequestParam String username, Model m) {
        if(Objects.equals(username, "")) {
            Iterable<Post> posts = postService.findAll();
            m.addAttribute("posts", posts);
        } else {
            User user = userService.findByUserName(username);

            if(user != null) {
                Iterable<Post> userPosts = postService.findByUserName(username);
                m.addAttribute("posts", userPosts);
            } else {
                m.addAttribute("posts", Collections.emptyList());
            }
        }

        return "landing";
    }

    @GetMapping("/employees")
    public String showHome() {
        return "home";
    }

    @GetMapping("/leaders")
    public String showLeaders() {
        return "leaders";
    }

    @GetMapping("/systems")
    public String showSystems() {
        return "systems";
    }

}









