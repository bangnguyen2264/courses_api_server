package com.example.course.security.author;

import com.example.course.permission.ResourcePermission;
import com.example.course.security.authen.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.ApplicationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class PermissionAspect {

    private final JwtService jwtService;
    private final ApplicationContext context;
    private final ExpressionParser parser = new SpelExpressionParser();

    // 1. Pointcut: Bắt cả trường hợp Single (@RequirePermission) và Container (@RequirePermissions)
    @Before("@annotation(com.example.course.security.author.RequirePermission) || " +
            "@annotation(com.example.course.security.author.RequirePermissions)")
    public void checkPermission(JoinPoint joinPoint) {

        // 2. Lấy thông tin User (Lấy 1 lần dùng cho tất cả các check)
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String token = jwtService.extractToken(request);
        String role = jwtService.extractRole(token);

        // Admin -> Bỏ qua tất cả các check
        if ("ROLE_ADMIN".equals(role)) {
            return;
        }

        Long currentUserId = jwtService.extractUserId(token);

        // 3. Lấy danh sách các Permission cần check (Support Repeatable)
        List<RequirePermission> permissions = getPermissions(joinPoint);

        // 4. Duyệt qua từng Permission (Logic AND: Phải thỏa mãn TẤT CẢ)
        for (RequirePermission perm : permissions) {
            checkSinglePermission(joinPoint, perm, currentUserId);
        }
    }

    // Helper: Thực hiện check 1 quyền cụ thể
    private void checkSinglePermission(JoinPoint joinPoint, RequirePermission permission, Long currentUserId) {
        // Lấy ID tài nguyên từ tham số hàm bằng SpEL
        Long resourceId = extractIdBySpEL(joinPoint, permission.idParam());

        // Nếu resourceId null (VD: filter params không truyền), ta có thể bỏ qua check hoặc chặn tùy logic.
        // Ở đây mình giữ logic cũ: Nếu cấu hình SpEL trỏ tới null -> coi như không tìm thấy ID -> Chặn hoặc Exception.
        // Tùy chỉnh: Nếu resourceId == null thì return (bỏ qua check này) nếu bạn muốn hỗ trợ optional filter.
        if (resourceId == null) {
            // Logic hiện tại: null là lỗi. Nếu muốn optional, hãy comment dòng throw và return;
            // throw new IllegalArgumentException("ID is null for expression: " + permission.idParam());
            return; // Ví dụ: Nếu examId null thì không check quyền exam
        }

        ResourcePermission checker = context.getBean(permission.resource());
        boolean allowed = checker.hasPermission(currentUserId, resourceId);

        if (!allowed) {
            throw new AccessDeniedException("You do not have permission to access resource defined by: " + permission.idParam());
        }
    }

    // Helper: Lấy list annotation dù là Single hay Repeated
    private List<RequirePermission> getPermissions(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        List<RequirePermission> result = new ArrayList<>();

        // Case 1: Container (Nhiều annotation gộp lại)
        RequirePermissions container = method.getAnnotation(RequirePermissions.class);
        if (container != null) {
            Collections.addAll(result, container.value());
        }

        // Case 2: Single (Chỉ có 1 annotation)
        RequirePermission single = method.getAnnotation(RequirePermission.class);
        if (single != null) {
            result.add(single);
        }

        return result;
    }

    private Long extractIdBySpEL(JoinPoint joinPoint, String spElExpression) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        StandardEvaluationContext evaluationContext = new StandardEvaluationContext();
        if (parameterNames != null) {
            for (int i = 0; i < parameterNames.length; i++) {
                evaluationContext.setVariable(parameterNames[i], args[i]);
            }
        }

        try {
            Object value = parser.parseExpression(spElExpression).getValue(evaluationContext);
            if (value == null) return null; // Trả về null để bên trên xử lý
            return Long.valueOf(value.toString());
        } catch (Exception e) {
            log.error("Failed to parse SpEL: {}", spElExpression, e);
            throw new IllegalArgumentException("Invalid permission ID configuration");
        }
    }
}