package projekti.Entities;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Post extends AbstractPersistable<Long> {

    @ManyToOne
    private Account author;
    
    @NotNull
    @Size(min = 1)
    private String content;
    
    @NotNull
    private String postDateTime;
    
    private Long timeMilliSeconds;
    private int likes;
    
    @ElementCollection
    private Set<String> likers = new HashSet<>();
}
