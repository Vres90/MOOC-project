package projekti.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import projekti.Services.PostService;

@Controller
public class WorkonnectPostController {
    
    @Autowired
    private PostService postService;
    
    @GetMapping("/posts")
    @Cacheable(value = "cache", sync=true)
    public String posts(Model model) {
        Pageable posts = PageRequest.of(0, 25, Sort.by("timeMilliSeconds").descending());
        model.addAttribute("posts", this.postService.findAll(posts));
        return "posts";
    }

    @PostMapping("/posts")
    public String addPost(@RequestParam String content) {
        postService.addPost(content);
        return "redirect:/posts";
    }
    
    @PostMapping("/posts/likepost")
    public String likePost(@RequestParam String liker, @RequestParam Long id) {
        postService.likePost(liker, id);
        return "redirect:/posts";
    }
}