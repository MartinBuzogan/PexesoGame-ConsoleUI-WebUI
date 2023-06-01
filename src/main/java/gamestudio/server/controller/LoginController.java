package gamestudio.server.controller;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.context.WebApplicationContext;


@Controller
@RequestMapping("/login")
@Scope(WebApplicationContext.SCOPE_SESSION)
public class LoginController {

    @GetMapping
    public String showLoginForm() {
        return "login";
    }
}