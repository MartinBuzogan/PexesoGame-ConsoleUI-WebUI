package gamestudio.server.registration;

import gamestudio.entity.AppUser;
import gamestudio.server.appuser.AppUserRole;
import gamestudio.server.appuser.AppUserService;
import gamestudio.server.email.EmailSender;
import gamestudio.entity.ConfirmationToken;
import gamestudio.server.registration.token.ConfirmationTokenService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class RegistrationService {
    private final AppUserService appUserService;
    private final EmailValidator emailValidator;
    private final EmailSender emailSender;
    private final ConfirmationTokenService confirmationTokenService;

    public String register(RegistrationRequest request) {

        boolean isValidEmail = !emailValidator.test(request.getEmail());
        System.out.println(request.getEmail());
        System.out.println(emailValidator.test(request.getEmail()));

        if(isValidEmail){
            throw new IllegalStateException("Email is not valid!");
        }
            String token = appUserService.signUpUser(
                    new AppUser(
                            request.getFirstName(),
                            request.getLastName(),
                            request.getEmail(),
                            request.getPassword(),
                            AppUserRole.USER
                    )
            );
        String link = "http://localhost:8080/registration/confirm/?token=" + token;
        emailSender.send(
                request.getEmail(),
                buildEmail(request.getFirstName(), link) );

        return  "success";
    }
    @Transactional
    public String confirmToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService
                .getToken(token)
                .orElseThrow(() ->
                        new IllegalStateException("token not found"));

        if (confirmationToken.getConfirmedAt() != null) {
            throw new IllegalStateException("email already confirmed");
        }

        LocalDateTime expiredAt = confirmationToken.getExpiredAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("token expired");
        }

        confirmationTokenService.setConfirmedAt(token);
        appUserService.enableAppUser(
                confirmationToken.getAppUser().getEmail());
        return "confirmed";
    }

    private String buildEmail(String name, String link) {
        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;background: linear-gradient(to bottom, rgba(255,255,255,0.15) 0%, rgba(0,0,0,0.15) 100%), radial-gradient(at top center, rgba(255,255,255,0.40) 0%, rgba(0,0,0,0.40) 120%) #989898;\">"
                + "<span style=\"display:none;font-size:1px;color:#1C1C1C;max-height:0\"></span>"
                + "<table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">"
                + "<tbody>"
                + "<tr>"
                + "<td width=\"100%\" height=\"53\" style=\"background-color: rgba(255, 255, 255, 0.40);\">"
                + "<table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">"
                + "<tbody>"
                + "<tr>"
                + "<td width=\"70\"  valign=\"middle\">"
                + "<table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">"
                + "<tbody>"
                + "<tr>"
                + "<td style=\"padding-left:10px\"></td>"
                + "<td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">"
                + "<span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#1C1C1C;text-decoration:none;vertical-align:top;display:inline-block\">Confirm your email</span>"
                + "</td>"
                + "</tr>"
                + "</tbody>"
                + "</table>"
                + "</td>"
                + "</tr>"
                + "</tbody>"
                + "</table>"
                + "</td>"
                + "</tr>"
                + "</tbody>"
                + "</table>"
                + "<table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">"
                + "<tbody>"
                + "<tr>"
                + "<td width=\"10\" height=\"10\" valign=\"middle\"></td>"
                + "<td>"
                + "<table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">"
                + "<tbody>"
                + "<tr>"
                + "<td bgcolor=\"#679436\" width=\"100%\" height=\"10\"></td>"
                + "</tr>"
                + "</tbody>"
                + "</table>"
                + "</td>"
                + "<td width=\"10\" valign=\"middle\" height=\"10\"></td>"
                + "</tr>"
                + "</tbody>"
                + "</table>"
                + "<table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">"
                + "<tbody>"
                + "<tr>"
                + "<td height=\"30\"><br></td>"
                + "</tr>"
                + "<tr>"
                + "<td width=\"10\" valign=\"middle\"><br></td>"
                + "<td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">"
                + "<p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi " + name + ",</p>"
                + "<p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Thank you for registering. Please click on the below link to activate your account:</p>"
                + "<blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\">"
                + "<p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"><a href=\"" + link + "\">Activate Now</a></p>"
                + "</blockquote>"
                + "<p>Link will expire in 15 minutes.</p>"
                + "<p>See you soon </p>"
                + "<p>Pexeso Game By M.B at KPI FEI TUKE</p>"
                + "</td>"
                + "<td width=\"10\" valign=\"middle\"><br></td>"
                + "</tr>"
                + "<tr>"
                + "<td height=\"30\"><br></td>"
                + "</tr>"
                + "</tbody>"
                + "</table>"
                + "<div class=\"yj6qo\"></div>"
                + "<div class=\"adL\"></div>"
                + "</div>";
    }
}
