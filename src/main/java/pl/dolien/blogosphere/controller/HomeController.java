package pl.dolien.blogosphere.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.dolien.blogosphere.entity.Post;
import pl.dolien.blogosphere.service.PostService;
import pl.dolien.blogosphere.service.UserService;

import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String showLanding(@RequestParam(required = false, name = "search") String title,
                              @RequestParam(required = false, defaultValue = "0") int page,
                              Model m, HttpServletRequest request) {
        int pageSize = 12;
        List<Post> posts;

        if (title != null) {
            posts = postService.findPostsByTitle(title);

            if (posts == null) {
                posts = Collections.emptyList();
            }

        } else {
            posts = postService.findAll();
        }

        Collections.reverse(posts);

        int start = page * pageSize;
        int end = Math.min(start + pageSize, posts.size());
        int totalPages = (int) Math.ceil((double) posts.size() / pageSize);
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

        if (title != null) {
            endPage = 1;
        }

        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            if (!paramName.equals("search") && !paramName.equals("page")) {
                return "error";
            }
        }

        if (endPage == 0) {
            endPage = 1;
        }

        if (page >= endPage) {
            return "error";
        }

        List<Post> paginatedPosts = posts.subList(start, end);

        m.addAttribute("posts", paginatedPosts);
        m.addAttribute("currentPage", page);
        m.addAttribute("totalPages", totalPages);
        m.addAttribute("startPage", startPage);
        m.addAttribute("endPage", endPage);
        m.addAttribute("amountPage", paginatedPosts.size());
        return "landing";
    }

    @PostMapping("/")
    public String search(@RequestParam("search") String title) {
        if (title.isEmpty()) {
            return "redirect:/";
        } else {
            return "redirect:/?search=" + title;
        }
    }
}









