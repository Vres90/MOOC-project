package projekti.Services;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Calendar;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import projekti.Entities.Post;
import projekti.Repositories.PostRepository;

@Transactional
@Service
public class PostService {
    
    @PostConstruct
    public void addTwoPosts() {
        
    }

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private AccountService accountService;

    @Autowired
    private HttpSession session;

    public Page<Post> findAll(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    public void addPost(String content) {
        Calendar calendar = Calendar.getInstance();
        postRepository.save(new Post(
                accountService.findByUsername(session.getAttribute("sesUsername").toString()),
                content,
                LocalDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT)),
                calendar.getTimeInMillis(),
                0,
                null));
    }
    
    public void likePost(String liker, Long id) {
        Post post = postRepository.findById(id).get();
        if (post != null && !post.getLikers().contains(liker)) {
            Set likers = post.getLikers();
            likers.add(liker);
            post.setLikers(likers);
            post.setLikes(post.getLikes()+2);
            postRepository.save(post);
        }
    }
}
