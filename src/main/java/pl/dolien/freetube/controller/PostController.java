package pl.dolien.freetube.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.dolien.freetube.dao.PostDao;
import pl.dolien.freetube.dao.RoleDao;
import pl.dolien.freetube.dao.UserDao;
import pl.dolien.freetube.entity.Post;
import pl.dolien.freetube.entity.Review;
import pl.dolien.freetube.entity.User;
import pl.dolien.freetube.service.PostService;
import pl.dolien.freetube.service.ReviewService;
import pl.dolien.freetube.service.UserService;
import pl.dolien.freetube.validation.WebPost;
import pl.dolien.freetube.validation.WebReview;
import pl.dolien.freetube.validation.WebUser;

import java.security.Principal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/post")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/upload")
    public String showUploadPostPage(Model m) {
        m.addAttribute("webPost", new WebPost());
        return "upload-post";
    }

    @PostMapping("/upload")
    public String uploadPost(@Valid @ModelAttribute("webPost") WebPost webPost, @RequestParam("privacy") String privacy, BindingResult theBindingResult, Authentication authentication) {

        if (theBindingResult.hasErrors()){
            return "upload-post";
        }

        String userName = authentication.getName();
        User user = userService.findByUserName(userName);
        webPost.setUser(user);
        webPost.setPrivacy(privacy);
        postService.save(webPost);

        return "redirect:/";
    }

    @GetMapping("/note")
    public String showNote(@RequestParam("postId") int id, Model m, Authentication authentication) {
        String userName = authentication.getName();
        User user = userService.findByUserName(userName);
        Post post = postService.findById(id);
        List<Review> reviews = reviewService.getAllByPostId(post.getId());

        if(post.getPrivacy().equals("private") && post.getUser() != user && !user.isAdmin(user)) {
            return "redirect:/error";
        }

        m.addAttribute("post", post);
        m.addAttribute("webReview", new WebReview());
        m.addAttribute("reviews", reviews);
        m.addAttribute("user", user);

        return "post";
    }

    @PostMapping("/note")
    public String addReview(@Valid @ModelAttribute("webReview") WebReview webReview, @RequestParam("postId") int postId,
                            BindingResult theBindingResult, Authentication authentication) {

        if (theBindingResult.hasErrors()){
            return "redirect:/post/note?postId=" + postId;
        }

        String userName = authentication.getName();
        User user = userService.findByUserName(userName);
        Post post = postService.findById(postId);

        webReview.setUser(user);
        webReview.setPost(post);
        reviewService.save(webReview);

        return "redirect:/post/note?postId=" + postId;
    }

    @GetMapping("/my")
    public String myPosts(Model m, Authentication authentication) {
        String userName = authentication.getName();
        List<Post> userPosts = postService.findByUserName(userName);

        if(userPosts == null) {
            userPosts = Collections.emptyList();
        }

        m.addAttribute("userPosts", userPosts);

        return "my-posts";
    }

    @GetMapping("/edit")
    public String edit(@RequestParam("postId") int postId, Model m, Authentication authentication) {
        String userName = authentication.getName();
        User user = userService.findByUserName(userName);
        Post post = postService.findById(postId);

        if(post.getPrivacy().equals("private") && post.getUser() != user && !user.isAdmin(user)) {
            return "redirect:/error";
        }

        WebPost webPost = new WebPost();
        webPost.setId(post.getId());
        webPost.setTitle(post.getTitle());
        webPost.setNote(post.getNote());
        webPost.setDescription(post.getDescription());
        webPost.setPrivacy(post.getPrivacy());
        webPost.setDate(post.getDate());
        webPost.setUser(post.getUser());
        webPost.setReviews(post.getReviews());

        m.addAttribute("webPost", webPost);

        return "upload-post";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam("postId") int postId, Authentication authentication) {
        String userName = authentication.getName();
        User user = userService.findByUserName(userName);
        Post post = postService.findById(postId);

        if(post.getPrivacy().equals("private") && post.getUser() != user && !user.isAdmin(user)) {
            return "redirect:/error";
        }

        postService.deleteById(postId);

        return "redirect:/post/my";
    }
}
