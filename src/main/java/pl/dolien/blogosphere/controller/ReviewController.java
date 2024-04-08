package pl.dolien.blogosphere.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.dolien.blogosphere.entity.Post;
import pl.dolien.blogosphere.dao.ReviewLikeDao;
import pl.dolien.blogosphere.entity.Review;
import pl.dolien.blogosphere.entity.User;
import pl.dolien.blogosphere.service.PostService;
import pl.dolien.blogosphere.service.ReviewService;
import pl.dolien.blogosphere.service.UserService;
import pl.dolien.blogosphere.validation.WebReview;

@Controller
@RequestMapping("/post/note/review")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Autowired
    private ReviewLikeDao reviewLikeDao;

    @PostMapping("/edit")
    public String edit(@ModelAttribute("webReview") WebReview webReview,
                       @RequestParam int reviewId,
                       BindingResult theBindingResult, Authentication authentication) {

        User user = userService.findByUserName(authentication.getName());
        Post post = postService.findById(webReview.getPostId());
        Review review = reviewService.findById(reviewId);

        if (user == null || post == null) {
            return "error";
        }

        if (theBindingResult.hasErrors()) {
            return "error";
        }

        if (!review.getUser().equals(user) && !user.isAdmin(user)) {
            return "error";
        }

        webReview.setPost(post);
        webReview.setUser(user);
        webReview.setId(reviewId);
        reviewService.save(webReview);

        return "redirect:/post/note?postId=" + webReview.getPostId();
    }

    @GetMapping("/delete")
    public String delete(@RequestParam int postId, @RequestParam int reviewId, Authentication authentication) {
        String userName = authentication.getName();
        User user = userService.findByUserName(userName);
        Review review = reviewService.findById(reviewId);

        if (user == null || review == null) {
            return "error";
        }
        if (!review.getUser().equals(user) && !user.isAdmin(user)) {
            return "error";
        }

        reviewService.deleteById(reviewId);

        return "redirect:/post/note?postId=" + postId;
    }

    @GetMapping("/addLike")
    public String addLike(@RequestParam int postId, @RequestParam int reviewId, Authentication authentication) {
        Review review = reviewService.findById(reviewId);
        User user = userService.findByUserName(authentication.getName());

        if (user == null || review == null) {
            return "error";
        }

        reviewLikeDao.addLike(review, user);

        return "redirect:/post/note?postId=" + postId;
    }

    @GetMapping("/removeLike")
    public String removeLike(@RequestParam int postId,
                             @RequestParam int reviewId, Authentication authentication) {
        try {
            Review review = reviewService.findById(reviewId);
            User user = userService.findByUserName(authentication.getName());

            if (user == null || review == null) {
                return "error";
            }

            reviewLikeDao.removeLike(review, user);

            return "redirect:/post/note?postId=" + postId;
        } catch (InvalidDataAccessApiUsageException e) {
            return "error";
        }
    }
}
