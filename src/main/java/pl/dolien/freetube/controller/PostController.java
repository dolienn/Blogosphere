package pl.dolien.freetube.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import pl.dolien.freetube.dao.PostDao;
import pl.dolien.freetube.dao.UserDao;
import pl.dolien.freetube.entity.Post;
import pl.dolien.freetube.entity.Review;
import pl.dolien.freetube.entity.User;
import pl.dolien.freetube.service.PostService;
import pl.dolien.freetube.service.ReviewService;
import pl.dolien.freetube.service.UserService;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/post")
public class PostController {

    @Autowired
    private PostDao videoDao;

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private UserDao userDao;

    @GetMapping("/upload")
    public String showUploadPostPage() {
        return "upload-post";
    }

    @PostMapping("/upload")
    public String uploadPost(@RequestParam("title") String title, @RequestParam("note") String note, @RequestParam("title") String privacy, Authentication authentication) {
        String userName = authentication.getName();
        User user = userService.findByUserName(userName);
        Post post = new Post(title, note, privacy);
        user.add(post);
        postService.save(post);

        return "redirect:/";
    }

    @GetMapping("/note")
    public String showNote(@RequestParam("postId") int id, Model m, Authentication authentication) {
        String userName = authentication.getName();
        User user = userService.findByUserName(userName);
        Post post = postService.findById(id);
        List<Review> reviews = reviewService.getAllByPostId(post.getId());

        m.addAttribute("post", post);
        m.addAttribute("reviews", reviews);
        m.addAttribute("user", user);

        return "post";
    }

    @PostMapping("/note")
    public String addReview(@RequestParam("postId") int postId ,@RequestParam("comment") String comment, Authentication authentication) {
        String userName = authentication.getName();
        User user = userService.findByUserName(userName);
        Post post = postService.findById(postId);

        Review review = new Review(comment);
        user.add(review);
        post.add(review);
        reviewService.save(review);


        return "redirect:/post/note?postId=" + postId;
    }

    @GetMapping("/my")
    public String myPosts(Model m, Authentication authentication) {
        String userName = authentication.getName();
        Iterable<Post> userPosts = postService.findByUserName(userName);

        m.addAttribute("userPosts", userPosts);

        return "my-posts";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam("postId") int id) {
        postService.deleteById(id);

        return "redirect:/post/my";
    }
}
