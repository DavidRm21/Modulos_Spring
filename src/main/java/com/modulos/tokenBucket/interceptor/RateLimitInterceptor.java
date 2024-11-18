package com.modulos.tokenBucket.interceptor;

import com.modulos.tokenBucket.service.RateLimitService;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Slf4j
public class RateLimitInterceptor implements HandlerInterceptor {
    private final RateLimitService rateLimitService;

    public RateLimitInterceptor(RateLimitService rateLimitService) {
        this.rateLimitService = rateLimitService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        String apiKey = getClientIdentifier(request);
        Bucket bucket = rateLimitService.resolveBucket(apiKey);

        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        log.warn("Tokens: {}",bucket.getAvailableTokens());

        if (probe.isConsumed()) {
            response.addHeader("X-Rate-Limit-Remaining",
                    String.valueOf(probe.getRemainingTokens()));
            return true;
        }

        long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;
        response.addHeader("X-Rate-Limit-Retry-After-Seconds",
                String.valueOf(waitForRefill));
        response.sendError(HttpStatus.TOO_MANY_REQUESTS.value(),
                "You have exhausted your API Request quota");
        return false;
    }

    private String getClientIdentifier(HttpServletRequest request) {
        return request.getRemoteAddr();
    }
}
