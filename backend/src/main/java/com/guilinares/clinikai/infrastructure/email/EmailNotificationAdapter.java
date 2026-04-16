package com.guilinares.clinikai.infrastructure.email;

import com.guilinares.clinikai.application.onboarding.ports.OnboardingNotificationPort;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailNotificationAdapter implements OnboardingNotificationPort {

    private final JavaMailSender mailSender;
    private final AdminActionTokenService tokenService;

    @Value("${app.admin.email:guimine.20@gmail.com}")
    private String adminEmail;

    @Value("${spring.mail.username:suporte@clinikai.com.br}")
    private String fromEmail;

    @Value("${app.backend-base-url:${APP_FRONTEND_BASE_URL:http://localhost:8080}}")
    private String backendBaseUrl;

    @Override
    @Async
    public void notifyNewClinicRegistration(UUID clinicId, String clinicName, String userName, String email, String whatsapp) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(adminEmail);
            helper.setSubject("[Clinikai] Nova clínica: " + clinicName);

            String approveToken = tokenService.generateToken(clinicId, "approve");
            String rejectToken = tokenService.generateToken(clinicId, "reject");

            String approveUrl = String.format("%s/api/admin/clinics/%s/approve?token=%s",
                    backendBaseUrl, clinicId, approveToken);
            String rejectUrl = String.format("%s/api/admin/clinics/%s/reject?token=%s",
                    backendBaseUrl, clinicId, rejectToken);

            String html = buildEmailHtml(clinicName, userName, email, whatsapp, approveUrl, rejectUrl);
            helper.setText(html, true);

            mailSender.send(mimeMessage);
            log.info("Email de notificação enviado para {} - Clínica: {}", adminEmail, clinicName);
        } catch (Exception e) {
            log.error("Falha ao enviar email de notificação para clínica {}: {}", clinicName, e.getMessage(), e);
        }
    }

    private String buildEmailHtml(String clinicName, String userName, String email,
                                   String whatsapp, String approveUrl, String rejectUrl) {
        return String.format("""
                <!DOCTYPE html>
                <html>
                <head><meta charset="UTF-8"></head>
                <body style="margin:0;padding:0;background:#EDEDCE;font-family:system-ui,-apple-system,Segoe UI,Roboto,sans-serif;">
                  <table width="100%%" cellpadding="0" cellspacing="0" style="padding:40px 20px;">
                    <tr><td align="center">
                      <table width="520" cellpadding="0" cellspacing="0" style="background:rgba(255,255,255,0.85);border:1px solid rgba(12,44,85,0.12);border-radius:14px;overflow:hidden;">

                        <!-- Header -->
                        <tr>
                          <td style="background:#0C2C55;padding:24px 28px;">
                            <span style="color:#EDEDCE;font-size:22px;font-weight:900;">ClinikAI</span>
                            <br>
                            <span style="color:rgba(237,237,206,0.7);font-size:13px;">Nova clínica aguardando aprovação</span>
                          </td>
                        </tr>

                        <!-- Body -->
                        <tr>
                          <td style="padding:28px;">
                            <table width="100%%" cellpadding="0" cellspacing="0" style="background:rgba(12,44,85,0.04);border:1px solid rgba(12,44,85,0.08);border-radius:10px;padding:18px;">
                              <tr><td>
                                <p style="margin:0 0 12px;font-size:14px;color:rgba(12,44,85,0.6);">DADOS DA CLÍNICA</p>
                                <table cellpadding="4" cellspacing="0" style="font-size:14px;color:#0b1b2b;">
                                  <tr>
                                    <td style="font-weight:600;padding-right:12px;">Clínica:</td>
                                    <td>%s</td>
                                  </tr>
                                  <tr>
                                    <td style="font-weight:600;padding-right:12px;">Responsável:</td>
                                    <td>%s</td>
                                  </tr>
                                  <tr>
                                    <td style="font-weight:600;padding-right:12px;">Email:</td>
                                    <td>%s</td>
                                  </tr>
                                  <tr>
                                    <td style="font-weight:600;padding-right:12px;">WhatsApp:</td>
                                    <td>%s</td>
                                  </tr>
                                </table>
                              </td></tr>
                            </table>

                            <!-- Buttons -->
                            <table width="100%%" cellpadding="0" cellspacing="0" style="margin-top:24px;">
                              <tr>
                                <td align="center" style="padding-right:8px;">
                                  <a href="%s"
                                     style="display:inline-block;padding:14px 32px;background:#296374;color:#ffffff;
                                            font-size:15px;font-weight:700;text-decoration:none;border-radius:12px;">
                                    Aprovar Clínica
                                  </a>
                                </td>
                                <td align="center" style="padding-left:8px;">
                                  <a href="%s"
                                     style="display:inline-block;padding:14px 32px;background:rgba(12,44,85,0.08);
                                            color:#0C2C55;font-size:15px;font-weight:700;text-decoration:none;
                                            border-radius:12px;border:1px solid rgba(12,44,85,0.15);">
                                    Rejeitar
                                  </a>
                                </td>
                              </tr>
                            </table>

                            <p style="margin-top:20px;font-size:12px;color:rgba(12,44,85,0.5);text-align:center;">
                              Estes links são exclusivos e seguros. Não compartilhe com terceiros.
                            </p>
                          </td>
                        </tr>

                      </table>
                    </td></tr>
                  </table>
                </body>
                </html>
                """, clinicName, userName, email, whatsapp, approveUrl, rejectUrl);
    }
}
