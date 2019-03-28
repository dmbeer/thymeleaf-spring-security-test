package com.example.thymeleafspringsecuritytest.ui;

import com.example.thymeleafspringsecuritytest.security.SpringUserDetailsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Mono;

@Controller
public class ChangePasswordController {

    private static final Logger log = LoggerFactory.getLogger(ChangePasswordController.class);
    private SpringUserDetailsRepository userDetailsRepository;
    private String password1 = "";
    private String password2 = "";

    public ChangePasswordController(SpringUserDetailsRepository repository) {
        this.userDetailsRepository = repository;
    }

    @GetMapping("/profile/change-password")
    public Mono<String> getChangePasswordPage(Model model) {
        model.addAttribute("metaTitle", "Change Password");
        return Mono.just("change-password");
    }

    @PostMapping(value = "/profile/change-password", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public Mono<String> changePasswordSubmit(Authentication authentication, @RequestBody MultiValueMap<String, String> formData) {
        formData.forEach((paramName, value) -> {
            if (paramName.equalsIgnoreCase("password1")) {
                password1 = value.get(0);
            } else if (paramName.equalsIgnoreCase("password2")) {
                password2 = value.get(0);
            }
        });
        if (password1.equals(password2)) {
            return userDetailsRepository.updatePassword(((UserDetails) authentication.getPrincipal()).getUsername(), password2).log("Created Data").then(Mono.just("redirect:/page/1")).log("Finished");
        } else {
            return Mono.just("/profile/change-password");
        }

    }
}
