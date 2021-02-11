package projekti.Controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import projekti.Entities.Account;
import projekti.Services.AccountService;

@Controller
@Log
public class WorkonnectAuthController {

    Cookie sesCookie = new Cookie("ID", UUID.randomUUID().toString());

    @Autowired
    private AccountService accountService;

    @Autowired
    private HttpSession session;

    @ModelAttribute
    private Account getAccount() {
        return new Account();
    }

    @GetMapping("/")
    public String index() {
        sesCookie.setMaxAge(69 * 69);
        session.setAttribute("cookie", sesCookie);
        String sesUsername = (String) session.getAttribute("sesUsername"); // Attribute sesUsername is created during login 
        if (sesUsername != null) {
            List contactRequestsList = accountService.getContactRequests(sesUsername);
            session.setAttribute("requests", contactRequestsList);
        }
        return "index";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password) {
        try {
            UserDetails userDetails = accountService.loadUserByUsername(email + "$" + password);
            String username = userDetails.getUsername();
            session.setAttribute("sesUsername", username);
            return "redirect:/";
        } catch (Exception e) {
            log.log(Level.INFO, e.toString());
            return "redirect:/register";
        }
    }

    @PostMapping("/register")
    public String register(@Valid
            @ModelAttribute Account account, BindingResult br) {
        if (br.hasErrors()) {
            return "redirect:/register";
        }
        try {
            accountService.addAccount(account);
        } catch (Exception e) {
            log.log(Level.INFO, e.toString());
            br.addError(new FieldError("account", "emailAddress", "There already exists an account with this email address!"));
        }
        return "redirect:/register";
    }

    @GetMapping("/logout0")
    public String logOut() {
        try {
            accountService.logout(session.getAttribute("sesUsername").toString());
            session.invalidate();
        } catch (Exception e) {
            log.log(Level.INFO, e.toString());
        }
        return "redirect:/";
    }
}
