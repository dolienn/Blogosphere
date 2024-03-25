package pl.dolien.freetube.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.dolien.freetube.entity.Post;
import pl.dolien.freetube.service.PostService;
import pl.dolien.freetube.service.UserService;

import java.util.Collections;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String showLanding(@RequestParam(required = false, name = "search") String title, Model m) {
        List<Post> posts;
        if (title != null) {
            posts = postService.findPostsByTitle(title);

            if(posts == null) {
                posts = Collections.emptyList();
            }
        }
        else {
            posts = postService.findAll();
        }

        Collections.reverse(posts);
        m.addAttribute("posts", posts);
        return "landing";
    }

    @PostMapping("/")
    public String search(@RequestParam("search") String title) {
        if(title.isEmpty()) {
            return "redirect:/";
        } else {
            return "redirect:/?search=" + title;
        }
    }

}









