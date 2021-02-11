package projekti.Entities;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account extends AbstractPersistable<Long> {
    @NotEmpty
    @Size(min = 2, max = 50)
    private String firstName;
    
    @NotEmpty
    @Size(min = 2, max = 50)
    private String lastName;
    
    @NotEmpty
    @Size(min = 4, max = 50)
    @Column(unique = true)
    private String username;
    
    @NotEmpty
    private String emailAddress;
    
    private String password;
    private String personalWebsiteAddress;
    
    @Lob
    private String biography;
    
    private LocalDate birthday;
    private Long phoneNumber;
    private boolean online;
    
    @ElementCollection
    private Map<String, Integer> skills = new HashMap<>();
    
    @ElementCollection
    @OneToMany
    private Set<Contact> contacts = new HashSet<>();
    
    @ElementCollection
    private Set<String> receivedContactRequests = new HashSet<>();
    
    @Lob
    private byte[] profileImage;
}
