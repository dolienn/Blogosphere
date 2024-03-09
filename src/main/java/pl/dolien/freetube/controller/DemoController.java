package pl.dolien.freetube.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DemoController {

    @GetMapping("/")
    public String showLanding() {
        return "landing";
    }

    @GetMapping("/employees")
    public String showHome() {
        return "home";
    }

    @GetMapping("/showUploadVideoPage")
    public String showUploadVideoPage() {
        return "upload-video";
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









