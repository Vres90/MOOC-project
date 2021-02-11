package projekti.Services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;
import projekti.Entities.Account;
import projekti.Entities.Contact;
import projekti.Repositories.AccountRepository;

@Transactional
@Service
public class AccountService implements UserDetailsService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passEncoder;

    public String checkPassword(String password) {
        return password;
    }

    String[] extensions = {"jpg", "jpeg", "png", "gif"};

    @Override
    public UserDetails loadUserByUsername(String credentials) throws UsernameNotFoundException {
        String email = credentials.split("[$]")[0];
        Account account = accountRepository.findByEmailAddress(email);

        if (account == null) {
            throw new UsernameNotFoundException("No account exists with this email address!");
        }

        String enteredPass = credentials.split("[$]")[1];
        if (!passEncoder.matches(enteredPass, account.getPassword())) {
            throw new UsernameNotFoundException("Wrong password.");
        }

        String username = account.getUsername();
        accountRepository.updateOnline(true, username);
        return new org.springframework.security.core.userdetails.User(
                account.getUsername(),
                account.getPassword(),
                true,
                true,
                true,
                true,
                Arrays.asList(new SimpleGrantedAuthority("USER")));
    }

    public void addAccount(Account account) throws Exception {
        if (findByEmail(account.getEmailAddress()) != null) {
            throw new Exception();
        }
        account.setPassword(passEncoder.encode(account.getPassword()));
        this.accountRepository.save(account);
    }

    @Cacheable(value = "users", sync = true)
    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    @Cacheable(value = "user", sync = true)
    public Account findByEmail(String email) throws Exception {
        return accountRepository.findByEmailAddress(email);
    }

    @Cacheable(value = "user", sync = true)
    public Account findByUsername(String username) throws UsernameNotFoundException {
        return accountRepository.findByUsername(username);
    }

    @Cacheable(value = "user", sync = true)
    public Account findById(Long id) throws Exception {
        return accountRepository.findById(id).get();
    }

    public void saveProfileImage(String username, MultipartFile file) throws IOException {
        String extension = file.getContentType().split("[/]")[1];
        if (!Arrays.asList(extensions).contains(extension)) {
            throw new IOException("Wrong filetype. Profile image must be JPEG, PNG or GIF.");
        }
        byte[] imageBytes = file.getBytes();
        accountRepository.saveProfilePicture(imageBytes, username);
    }

    public void logout(String username) {
        accountRepository.updateOnline(false, username);
    }

    public void updateInfo(String attribute, String newValue, String username) throws Exception {
        switch (attribute) {
            case "username":
                accountRepository.updateUsername(newValue, username);
                break;
            case "biography":
                accountRepository.updateBio(newValue, username);
                break;
            case "personalWebsiteAddress":
                accountRepository.updateWebsite(newValue, username);
                break;
        }
    }

    public void addContact(String senderUsername, String receiverUsername) throws Exception {
        Account requestReceiver = accountRepository.findByUsername(receiverUsername);
        if (requestReceiver != null) {
            Set receiversRequests = requestReceiver.getReceivedContactRequests();
            receiversRequests.add(senderUsername);
            requestReceiver.setReceivedContactRequests(receiversRequests);
            accountRepository.save(requestReceiver);
        }
    }

    public List<String> getContactRequests(String sesUsername) {
        Set contactRequestsHashSet = this.findByUsername(sesUsername).getReceivedContactRequests();
        return new ArrayList<>(contactRequestsHashSet);
    }

    public void handleContactRequest(String sesUsername, String requesterUsername, int i) throws Exception {
        if (i == 1) {
            Account accepter = this.findByUsername(sesUsername);
            Set contactList = accepter.getContacts();
            contactList.add(new Contact(this.findByUsername(sesUsername), this.findByUsername(requesterUsername)));
            accepter.setContacts(contactList);
            accountRepository.save(accepter);

            Account requester = this.findByUsername(requesterUsername);
            Set contactList2 = requester.getContacts();
            contactList2.add(new Contact(this.findByUsername(requesterUsername), this.findByUsername(sesUsername)));
            requester.setContacts(contactList2);
            accountRepository.save(requester);
        }
    }
    
    public void likeSkill(String skill, String liker, String targetUsername) throws Exception {
        Account target = this.findByUsername(targetUsername);
        Map skills = target.getSkills();
        int likes = (int) skills.get(skill) + 1;
        skills.replace(skill, likes);
        target.setSkills(skills);
        accountRepository.save(target);
    }
}
