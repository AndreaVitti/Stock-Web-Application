package com.project.backend.interceptor;

import com.project.backend.service.BucketService;
import com.project.backend.utility.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class RateLimitInterceptor implements HandlerInterceptor {
    private final BucketService bucketService;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        String clientKey = getClientId(request);
        Bucket bucket = bucketService.resolveBucket(clientKey);
        boolean consumeStatus = bucket.consume();
        if (consumeStatus) {
            response.addHeader("X-Rate-Limit-Remaining",
                    String.valueOf(bucket.getRemainingTokens()));
            return true;
        }
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.addHeader("X-Rate-Limit-Remaining", "0");
        response.addHeader("Retry-After", String.valueOf(bucket.getTimeBeforeNextRefill()));
        response.getWriter().write("{\"error\": \"Rate limit exceeded\"}");
        response.setContentType("application/json");
        return false;
    }

    private String getClientId(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isEmpty()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
