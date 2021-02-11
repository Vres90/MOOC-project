package projekti.Controllers;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import lombok.extern.java.Log;
import javax.annotation.PostConstruct;
import org.springframework.cache.annotation.Cacheable;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import projekti.Entities.Account;
import projekti.Repositories.AccountRepository;
import projekti.Services.AccountService;

@Controller
@Log
public class WorkonnectAccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private HttpSession session;

    @PostConstruct
    public void addFour() throws Exception {
        Map<String, Integer> skills = new HashMap<>();
        skills.put("Java", 7);
        accountService.addAccount(new Account("Patrick", "Park", "parkpat", "parkpatrick@workonnect.com", "pass", "https://vrescendo.com", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.", LocalDate.of(1990, 6, 5), 899804934L, false, skills, null, null, null));
        accountService.addAccount(new Account("Miina", "Savolainen", "savomii", "miina@workonnect.com", "pass", "https://hs.fi", "Moikkamoi! Etsin töitä ja uusia kontakteja. Harrastan viulunsoittoa ja laulamista.", LocalDate.of(1993, 5, 12), 599804934L, false, skills, null, null, null));
        accountService.addAccount(new Account("Zhen", "Li", "lizhen", "zhen@workonnect.com", "pass", "https://google.com", "Moikkamoi! Etsin töitä ja uusia kontakteja. Harrastan lenkkeilyä ja piirtämistä.", LocalDate.of(1996, 3, 10), 745638876L, false, skills, null, null, null));
        accountService.addAccount(new Account("Tuomo", "Hentunen", "henttuom", "tuomojes@workonnect.com", "pass", "https://msn.com", "Moikkamoi! Etsin töitä ja uusia kontakteja. Harrastan amerikkalaista jalkapalloa ja salilla käyntiä.", LocalDate.of(1986, 1, 3), 676804845L, false, skills, null, null, null));
    }

    @GetMapping("/account")
    public String account() {
        return "redirect:/";
    }

    @GetMapping("/accounts")
    public String accounts(Model model) {
        model.addAttribute("accounts", accountService.findAll());
        return "accounts";
    }

    @Transactional
    @GetMapping("/account/{username}")
    @Cacheable(value = "cache", sync = true)
    public String viewAccount(Model model, @PathVariable String username) {
        try {
            Account account = accountService.findByUsername(username);
            model.addAttribute("username", username);
            model.addAttribute("account", account);
            LocalDate now = LocalDate.now();
            int age = Period.between(account.getBirthday(), now).getYears();
            model.addAttribute("accountUserAge", age);
        } catch (Exception e) {
            log.log(Level.INFO, e.toString());
        }
        return "account";
    }

    @GetMapping("/account/{username}/img")
    @Cacheable(value = "profileImage")
    public void showProfileImage(@PathVariable String username, HttpServletResponse response) {
        try {
            byte[] image = accountRepository.findByUsername(username).getProfileImage(); // Must use repository directly here. Byte array specifically is null when retrieved via service...
            response.setContentType("image/jpeg, image/jpg, image/png, image/gif");
            if (image == null) {
                response.getOutputStream().write(this.getClass().getResourceAsStream("/public/img/blankprofilepic.jpg").readAllBytes());
            } else {
                response.getOutputStream().write(image);
            }
            response.getOutputStream().close();
        } catch (Exception e) {
            log.log(Level.INFO, e.toString());
        }
    }

    @PostMapping("/uploadProfileImage")
    public String uploadProfileImage(@RequestParam("file") MultipartFile file) {
        String sesUsername = (String) session.getAttribute("sesUsername");
        try {
            accountService.saveProfileImage(sesUsername, file);
        } catch (IOException ioe) {
            log.log(Level.INFO, ioe.toString());
        }
        return "redirect:/account/" + sesUsername;
    }

    @PostMapping("/account/edit/{attribute}")
    public String editAccountInfo(@PathVariable String attribute, @RequestParam String newAttributeValue) {
        String sesUsername = (String) session.getAttribute("sesUsername");
        try {
            accountService.updateInfo(attribute, newAttributeValue, sesUsername);
            if (attribute.equals("username")) {
                session.setAttribute("sesUsername", newAttributeValue);
            }
        } catch (Exception e) {
            log.log(Level.INFO, e.toString());
        }
        return "redirect:/account/" + session.getAttribute("sesUsername");
    }

    @PostMapping("/account/sendcontactrequest")
    public String addContact(@RequestParam String receiverUsername) {
        try {
            accountService.addContact((String) session.getAttribute("sesUsername"), receiverUsername);
        } catch (Exception e) {
            log.log(Level.INFO, e.toString());
        }
        return "redirect:/account/" + receiverUsername;
    }

    @PostMapping("/account/handlecontactrequest")
    public String handleContactRequest(@RequestParam String requesterUsername, @RequestParam int i) {
        String sesUsername = (String) session.getAttribute("sesUsername");
        try {
            accountService.handleContactRequest(sesUsername, requesterUsername, i);
        } catch (Exception e) {
            log.log(Level.INFO, e.toString());
        }
        session.removeAttribute("requests");
        return "redirect:/account/" + sesUsername;
    }

    @PostMapping("/account/likeskill") // Should have implemented skill as entity
    public String likeSkill(@RequestParam String skill, @RequestParam String liker, @RequestParam String targetAccount) {
        try {
            accountService.likeSkill(skill, liker, targetAccount);
        } catch (Exception e) {
            log.log(Level.INFO, e.toString());
        }
        return "redirect:/account/" + targetAccount;
    }
}
