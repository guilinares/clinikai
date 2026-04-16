package com.guilinares.clinikai.infrastructure.billing;

import com.guilinares.clinikai.infrastructure.data.repositories.ClinicBillingRepository;
import com.guilinares.clinikai.infrastructure.security.SecurityUserPrincipal;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BillingEnforcementFilter extends OncePerRequestFilter {

    private final ClinicBillingRepository billingRepo;

    // rotas liberadas sem billing ACTIVE
    private static final List<String> WHITELIST = List.of(
            "/api/auth",
            "/api/me",
            "/api/billing",
            "/api/webhooks/asaas",
            "/actuator",
            "/error",
            "/api/kb",
            "/api/clinics/*/kb"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        if (path.startsWith("/api/auth")) return true;
        if (path.startsWith("/api/billing/subscribe/basic")) return true;
        if (path.startsWith("/api/webhooks/asaas")) return true;
        if (path.startsWith("/api/onboarding")) return true;
        if (path.startsWith("/api/admin")) return true;
        // ✅ KB liberado
        if (path.contains("/kb")) return true;
        return WHITELIST.stream().anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, ServletException {

        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof SecurityUserPrincipal principal)) {
            filterChain.doFilter(request, response);
            return;
        }

        var clinicId = principal.clinicId();

        var status = billingRepo.findStatusByClinicId(clinicId).orElse("NO_SUBSCRIPTION");
        if (!"ACTIVE".equals(status)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("""
              {"code":"BILLING_INACTIVE","message":"Assinatura inativa. Regularize o pagamento para continuar."}
            """);
            return;
        }

        filterChain.doFilter(request, response);
    }
}