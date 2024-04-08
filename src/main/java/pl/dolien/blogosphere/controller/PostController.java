package pl.dolien.blogosphere.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.dolien.blogosphere.entity.Post;
import pl.dolien.blogosphere.dao.PostLikeDao;
import pl.dolien.blogosphere.entity.PostLike;
import pl.dolien.blogosphere.entity.Review;
import pl.dolien.blogosphere.entity.User;
import pl.dolien.blogosphere.service.PostService;
import pl.dolien.blogosphere.service.ReviewService;
import pl.dolien.blogosphere.service.UserService;
import pl.dolien.blogosphere.validation.WebPost;
import pl.dolien.blogosphere.validation.WebReview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
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

    @Autowired
    private PostLikeDao postLikeDao;

    @GetMapping("/upload")
    public String showUploadPostPage(Model m) {
        m.addAttribute("webPost", new WebPost());
        return "upload-post";
    }

    @PostMapping("/upload")
    public String uploadPost(@Valid @ModelAttribute("webPost") WebPost webPost,
                             BindingResult theBindingResult, Authentication authentication) {

        if (theBindingResult.hasErrors()) {
            return "upload-post";
        }

        String userName = authentication.getName();
        User user = userService.findByUserName(userName);
        if (user != null) {
            webPost.setUser(user);
        }
        postService.save(webPost);

        return "redirect:/";
    }

    @GetMapping("/note")
    public String showNote(@RequestParam(required = false, defaultValue = "0") int page,
                           @RequestParam("postId") int id, Model m, Authentication authentication,
                           HttpServletRequest request) {
        int pageSize = 8;

        String userName = authentication.getName();
        User user = userService.findByUserName(userName);

        Post post = postService.findById(id);

        if (post == null || user == null) {
            return "error";
        }

        List<Review> reviews = reviewService.findAllByPostId(post.getId());

        if (post.getPrivacy().equals("private") && post.getUser() != user && !user.isAdmin(user)) {
            return "error";
        }

        int start = page * pageSize;
        int end = Math.min(start + pageSize, reviews.size());
        int totalPages = (int) Math.ceil((double) reviews.size() / pageSize);
        start = Math.max(0, start);
        int endPage = totalPages;
        int startPage = 0;

        if (totalPages > 3) {
            totalPages = 3;
        }

        if (page > totalPages - 1) {
            startPage = page;
        }

        if (page > endPage - 4) {
            startPage = endPage - 3;
        }

        if (totalPages < 3) {
            startPage = 0;
        }

        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            if (!paramName.equals("postId") && !paramName.equals("page")) {
                return "error";
            }
        }

        if (endPage == 0) {
            endPage = 1;
        }

        if (page >= endPage) {
            return "error";
        }

        List<Review> paginatedReviews = reviews.subList(start, end);

        WebReview webReview = new WebReview();
        webReview.setPostId(post.getId());

        m.addAttribute("post", post);
        m.addAttribute("webReview", webReview);
        m.addAttribute("reviews", paginatedReviews);
        m.addAttribute("user", user);
        m.addAttribute("currentPage", page);
        m.addAttribute("totalPages", totalPages);
        m.addAttribute("startPage", startPage);
        m.addAttribute("endPage", endPage);
        m.addAttribute("amountPage", paginatedReviews.size());
        return "post";
    }

    @PostMapping("/note")
    public String addReview(@Valid @ModelAttribute("webReview") WebReview webReview,
                            BindingResult theBindingResult, Authentication authentication, Model m) {

        User user = userService.findByUserName(authentication.getName());
        Post post = postService.findById(webReview.getPostId());

        if (post == null || user == null) {
            return "error";
        }

        if (theBindingResult.hasErrors()) {
            WebReview theWebReview = new WebReview();
            theWebReview.setPostId(post.getId());

            m.addAttribute("user", user);
            m.addAttribute("post", post);
            m.addAttribute("webReview", theWebReview);
            m.addAttribute("reviews", post.getReviews());
            m.addAttribute("error", true);
            return "post";
        }

        webReview.setPost(post);
        webReview.setUser(user);
        reviewService.save(webReview);

        return "redirect:/post/note?postId=" + webReview.getPostId();
    }

    @GetMapping("/my")
    public String myPosts(@RequestParam(required = false, defaultValue = "0") int page, Model m,
                          Authentication authentication, HttpServletRequest request) {
        int pageSize = 12;
        User user = userService.findByUserName(authentication.getName());

        if (user == null) {
            return "error";
        }

        List<Post> userPosts = postService.findByUserName(user.getUserName());

        if (userPosts == null) {
            userPosts = Collections.emptyList();
        }

        Collections.reverse(userPosts);

        int start = page * pageSize;
        int end = Math.min(start + pageSize, userPosts.size());
        int totalPages = (int) Math.ceil((double) userPosts.size() / pageSize);
        start = Math.max(0, start);
        int endPage = totalPages;
        int startPage = 0;

        if (totalPages > 3) {
            totalPages = 3;
        }

        if (page > totalPages - 1) {
            startPage = page;
        }

        if (page > endPage - 4) {
            startPage = endPage - 3;
        }

        if (totalPages < 3) {
            startPage = 0;
        }

        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            if (!paramName.equals("page")) {
                return "error";
            }
        }

        if (endPage == 0) {
            endPage = 1;
        }

        if (page >= endPage) {
            return "error";
        }

        List<Post> paginatedPosts = userPosts.subList(start, end);

        m.addAttribute("pageURL", "my");
        m.addAttribute("user", user);
        m.addAttribute("userPosts", paginatedPosts);
        m.addAttribute("currentPage", page);
        m.addAttribute("totalPages", totalPages);
        m.addAttribute("startPage", startPage);
        m.addAttribute("endPage", endPage);
        m.addAttribute("chosen", true);
        m.addAttribute("amountPage", paginatedPosts.size());
        return "my-posts";
    }

    @GetMapping("/favorite")
    public String favoritePosts(@RequestParam(required = false, defaultValue = "0") int page, Model m,
                                Authentication authentication, HttpServletRequest request) {
        int pageSize = 12;
        User user = userService.findByUserName(authentication.getName());

        if (user == null) {
            return "error";
        }

        List<Post> userPosts = new ArrayList<>();
        List<PostLike> userLikes = user.getLikes();

        for (PostLike postLike : userLikes) {
            if (postLike.getUser().equals(user)) {
                userPosts.add(postLike.getPost());
            }
        }

        Collections.reverse(userPosts);

        int start = page * pageSize;
        int end = Math.min(start + pageSize, userPosts.size());
        int totalPages = (int) Math.ceil((double) userPosts.size() / pageSize);
        start = Math.max(0, start);
        int endPage = totalPages;
        int startPage = 0;

        if (totalPages > 3) {
            totalPages = 3;
        }

        if (page > totalPages - 1) {
            startPage = page;
        }

        if (page > endPage - 4) {
            startPage = endPage - 3;
        }

        if (totalPages < 3) {
            startPage = 0;
        }

        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            if (!paramName.equals("page")) {
                return "error";
            }
        }

        if (endPage == 0) {
            endPage = 1;
        }

        if (page >= endPage) {
            return "error";
        }

        List<Post> paginatedPosts = userPosts.subList(start, end);

        m.addAttribute("pageURL", "favorite");
        m.addAttribute("user", user);
        m.addAttribute("userPosts", paginatedPosts);
        m.addAttribute("currentPage", page);
        m.addAttribute("totalPages", totalPages);
        m.addAttribute("startPage", startPage);
        m.addAttribute("endPage", endPage);
        m.addAttribute("chosen", false);
        m.addAttribute("amountPage", paginatedPosts.size());
        return "my-posts";
    }

    @GetMapping("/edit")
    public String edit(@RequestParam("postId") int postId, Authentication authentication, Model m) {
        String userName = authentication.getName();
        User user = userService.findByUserName(userName);
        Post post = postService.findById(postId);

        if (post == null || user == null) {
            return "error";
        }

        if (post.getPrivacy().equals("private") && post.getUser() != user && !user.isAdmin(user)) {
            return "error";
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

        if (post == null || user == null) {
            return "error";
        }

        if (post.getPrivacy().equals("private") && post.getUser() != user && !user.isAdmin(user)) {
            return "error";
        }

        postService.deleteById(postId);

        return "redirect:/post/my";
    }

    @GetMapping("/addLike")
    public String addLike(@RequestParam int postId, Authentication authentication) {
        Post post = postService.findById(postId);
        User user = userService.findByUserName(authentication.getName());

        if (post == null || user == null) {
            return "error";
        }

        postLikeDao.addLike(post, user);

        return "redirect:/post/note?postId=" + postId;
    }

    @GetMapping("/removeLike")
    public String removeLike(@RequestParam int postId, Authentication authentication) {
        try {
            Post post = postService.findById(postId);
            User user = userService.findByUserName(authentication.getName());

            if (post == null || user == null) {
                return "error";
            }

            postLikeDao.removeLike(post, user);

            return "redirect:/post/note?postId=" + postId;
        } catch (InvalidDataAccessApiUsageException e) {
            return "error";
        }
    }
}
