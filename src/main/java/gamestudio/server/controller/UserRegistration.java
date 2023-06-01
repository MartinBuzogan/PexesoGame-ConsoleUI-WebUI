package gamestudio.server.controller;

import gamestudio.server.registration.RegistrationRequest;
import gamestudio.server.registration.RegistrationService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(path = "/registration")
public class UserRegistration {
    private int registerSucc = 0;
    private final RegistrationService registrationService;

    public UserRegistration(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }
    @GetMapping
    public String showRegForm() {
        return "registration";
    }

    @PostMapping
    public String register(@RequestBody RegistrationRequest request){
        try{
            registrationService.register(request);
            registerSucc = 1;
        }catch(Exception e){
            registerSucc = 2;
        }
        return "/registration";
    }
    @RequestMapping(value = "/succ", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String isRegisterSucc(){
        var sb = new StringBuilder();
        if(registerSucc == 1){
            sb.append("<div class=\"alert alert-success\">\nRegistration successful.Please Check your email\n</div>");
        }
        else if(registerSucc == 2){
            sb.append("<div class=\"alert alert-danger\">\nRegistration failed. Please try again later.\n</div>");
        }
        registerSucc = 0;
        return sb.toString();
    }

    @GetMapping(path = "confirm")
    public String confirm(@RequestParam("token") String token) {
        try {
            registrationService.confirmToken(token);
        }catch (Exception e){
            System.out.println(e);
        }
        return "confirm";
    }

}
