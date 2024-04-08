package pl.dolien.blogosphere.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.dolien.blogosphere.entity.Post;
import pl.dolien.blogosphere.entity.Review;
import pl.dolien.blogosphere.entity.User;
import pl.dolien.blogosphere.service.PostService;
import pl.dolien.blogosphere.service.ReviewService;
import pl.dolien.blogosphere.service.UserService;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Autowired
    private ReviewService reviewService;

    @GetMapping("")
    public String admin(Model m) {
        return users(m);
    }

    @GetMapping("/users")
    public String users(Model m) {
        Iterable<User> users = userService.findAll();

        m.addAttribute("users", users);
        return "/admin/users";
    }

    @GetMapping("/user/delete")
    public String deleteUser(@RequestParam("theUserName") String userName) {
        userService.deleteUser(userName);

        return "redirect:/admin/users";
    }

    @GetMapping("/post/delete")
    public String deletePost(@RequestParam("postId") int postId,
                             @RequestParam("theUserName") String userName) {
        postService.deleteById(postId);

        return "redirect:/admin/user-posts?theUserName=" + userName;
    }

    @GetMapping("/review/delete")
    public String deleteReview(@RequestParam("reviewId") int reviewId,
                               @RequestParam("theUserName") String userName) {
        reviewService.deleteById(reviewId);

        return "redirect:/admin/user-reviews?theUserName=" + userName;
    }

    @GetMapping("/user-posts")
    public String userPosts(@RequestParam("theUserName") String userName, Model m) {
        List<Post> posts = postService.findByUserName(userName);
        User user = userService.findByUserName(userName);

        m.addAttribute("user", user);
        m.addAttribute("posts", posts);
        return "/admin/posts";
    }

    @GetMapping("/user-reviews")
    public String userReviews(@RequestParam("theUserName") String theUserName, Model m) {
        User user = userService.findByUserName(theUserName);
        List<Review> reviews = reviewService.findAllByUserId(user.getId());

        m.addAttribute("user", user);
        m.addAttribute("reviews", reviews);
        return "/admin/reviews";
    }

    @GetMapping("/post-reviews")
    public String postReviews(@RequestParam("postId") int postId,
                              @RequestParam("theUserName") String theUserName, Model m) {
        User user = userService.findByUserName(theUserName);
        Post post = postService.findById(postId);
        List<Review> reviews = reviewService.findAllByPostId(postId);

        m.addAttribute("post", post);
        m.addAttribute("user", user);
        m.addAttribute("reviews", reviews);
        return "/admin/reviews";
    }
}
