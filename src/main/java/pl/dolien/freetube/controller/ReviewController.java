package pl.dolien.freetube.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pl.dolien.freetube.entity.Post;
import pl.dolien.freetube.entity.Review;
import pl.dolien.freetube.entity.User;
import pl.dolien.freetube.service.PostService;
import pl.dolien.freetube.service.ReviewService;
import pl.dolien.freetube.service.UserService;

@Controller
@RequestMapping("/post/note/review")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @GetMapping("/delete")
    public String delete(@RequestParam("postId") int postId, @RequestParam("reviewId") int reviewId, Authentication authentication) {
        reviewService.deleteById(reviewId);

        return "redirect:/post/note?postId=" + postId;
    }
}
