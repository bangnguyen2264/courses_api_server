package com.example.course.exception;

import com.example.course.model.response.ErrorDetailResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // ============================================
    // REDIS EXCEPTIONS - CRITICAL FOR DEBUGGING
    // ============================================

    /**
     * Handle Redis System Exception
     * This is the most common Redis error for serialization and connection issues
     */
    @ExceptionHandler(RedisSystemException.class)
    public ResponseEntity<ErrorDetailResponse> handleRedisSystemException(
            RedisSystemException exception,
            HttpServletRequest request) {

        log.error("╔════════════════════════════════════════════════════════════════╗");
        log.error("║           REDIS SYSTEM EXCEPTION DETECTED                      ║");
        log.error("╚════════════════════════════════════════════════════════════════╝");
        log.error("📍 Endpoint: {} {}", request.getMethod(), request.getRequestURI());
        log.error("🔴 Error Message: {}", exception.getMessage());

        // Get root cause
        Throwable rootCause = getRootCause(exception);
        log.error("🎯 Root Cause: {} - {}",
                rootCause.getClass().getName(),
                rootCause.getMessage());

        // Analyze specific Redis error
        String diagnosis = diagnoseRedisError(exception, rootCause);
        log.error("💡 Diagnosis: {}", diagnosis);

        // Log full stack trace for debugging
        log.error("📋 Full Stack Trace:");
        log.error(getStackTraceAsString(exception));

        // Log thread and timing info
        log.error("🧵 Thread: {}", Thread.currentThread().getName());
        log.error("⏰ Timestamp: {}", Instant.now());

        log.error("╔════════════════════════════════════════════════════════════════╗");
        log.error("║              END REDIS EXCEPTION DETAILS                       ║");
        log.error("╚════════════════════════════════════════════════════════════════╝");

        ErrorDetailResponse error = new ErrorDetailResponse(
                Instant.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Redis System Error",
                diagnosis + " | Original: " + exception.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /**
     * Handle Redis Connection Failure
     */
    @ExceptionHandler(RedisConnectionFailureException.class)
    public ResponseEntity<ErrorDetailResponse> handleRedisConnectionFailure(
            RedisConnectionFailureException exception,
            HttpServletRequest request) {

        log.error("╔════════════════════════════════════════════════════════════════╗");
        log.error("║        REDIS CONNECTION FAILURE DETECTED                       ║");
        log.error("╚════════════════════════════════════════════════════════════════╝");
        log.error("📍 Endpoint: {} {}", request.getMethod(), request.getRequestURI());
        log.error("🔴 Error: {}", exception.getMessage());

        Throwable rootCause = getRootCause(exception);
        log.error("🎯 Root Cause: {}", rootCause.getMessage());

        String diagnosis = analyzeConnectionFailure(exception);
        log.error("💡 Diagnosis: {}", diagnosis);
        log.error("📋 Stack Trace:");
        log.error(getStackTraceAsString(exception));
        log.error("╚════════════════════════════════════════════════════════════════╝");

        ErrorDetailResponse error = new ErrorDetailResponse(
                Instant.now(),
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                "Redis Connection Failed",
                diagnosis,
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
    }

    /**
     * Handle Redis Serialization Exception
     */
    @ExceptionHandler(SerializationException.class)
    public ResponseEntity<ErrorDetailResponse> handleSerializationException(
            SerializationException exception,
            HttpServletRequest request) {

        log.error("╔════════════════════════════════════════════════════════════════╗");
        log.error("║          SERIALIZATION EXCEPTION DETECTED                      ║");
        log.error("╚════════════════════════════════════════════════════════════════╝");
        log.error("📍 Endpoint: {} {}", request.getMethod(), request.getRequestURI());
        log.error("🔴 Error: {}", exception.getMessage());

        Throwable rootCause = getRootCause(exception);
        log.error("🎯 Root Cause: {}", rootCause.getMessage());

        String diagnosis = analyzeSerializationError(exception);
        log.error("💡 Diagnosis: {}", diagnosis);
        log.error("📋 Stack Trace:");
        log.error(getStackTraceAsString(exception));
        log.error("╚════════════════════════════════════════════════════════════════╝");

        ErrorDetailResponse error = new ErrorDetailResponse(
                Instant.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Serialization Error",
                diagnosis,
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    // ============================================
    // DATABASE EXCEPTIONS
    // ============================================

    /**
     * Handle Database Access Exceptions
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorDetailResponse> handleDataAccessException(
            DataAccessException exception,
            HttpServletRequest request) {

        log.error("╔════════════════════════════════════════════════════════════════╗");
        log.error("║          DATABASE ACCESS EXCEPTION DETECTED                    ║");
        log.error("╚════════════════════════════════════════════════════════════════╝");
        log.error("📍 Endpoint: {} {}", request.getMethod(), request.getRequestURI());
        log.error("🔴 Error: {}", exception.getMessage());

        Throwable rootCause = getRootCause(exception);
        log.error("🎯 Root Cause: {}", rootCause.getMessage());

        String diagnosis = analyzeDatabaseError(exception);
        log.error("💡 Diagnosis: {}", diagnosis);
        log.error("📋 Stack Trace:");
        log.error(getStackTraceAsString(exception));
        log.error("╚════════════════════════════════════════════════════════════════╝");

        ErrorDetailResponse error = new ErrorDetailResponse(
                Instant.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Database Error",
                diagnosis,
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /**
     * Handle SQL Exceptions
     */
    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ErrorDetailResponse> handleSQLException(
            SQLException exception,
            HttpServletRequest request) {

        log.error("╔════════════════════════════════════════════════════════════════╗");
        log.error("║              SQL EXCEPTION DETECTED                            ║");
        log.error("╚════════════════════════════════════════════════════════════════╝");
        log.error("📍 Endpoint: {} {}", request.getMethod(), request.getRequestURI());
        log.error("🔴 SQL State: {}", exception.getSQLState());
        log.error("🔴 Error Code: {}", exception.getErrorCode());
        log.error("🔴 Message: {}", exception.getMessage());
        log.error("📋 Stack Trace:");
        log.error(getStackTraceAsString(exception));
        log.error("╚════════════════════════════════════════════════════════════════╝");

        String diagnosis = "SQL Error [" + exception.getSQLState() + "] Code: " + exception.getErrorCode();

        ErrorDetailResponse error = new ErrorDetailResponse(
                Instant.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "SQL Error",
                diagnosis + " - " + exception.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    // ============================================
    // NULL POINTER & ILLEGAL ARGUMENT EXCEPTIONS
    // ============================================

    /**
     * Handle NullPointerException
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorDetailResponse> handleNullPointerException(
            NullPointerException exception,
            HttpServletRequest request) {

        log.error("╔════════════════════════════════════════════════════════════════╗");
        log.error("║          NULL POINTER EXCEPTION DETECTED                       ║");
        log.error("╚════════════════════════════════════════════════════════════════╝");
        log.error("📍 Endpoint: {} {}", request.getMethod(), request.getRequestURI());
        log.error("🔴 Error: {}", exception.getMessage());

        StackTraceElement[] stackTrace = exception.getStackTrace();
        if (stackTrace.length > 0) {
            StackTraceElement element = stackTrace[0];
            log.error("📂 File: {}", element.getFileName());
            log.error("📝 Class: {}", element.getClassName());
            log.error("⚙️ Method: {}", element.getMethodName());
            log.error("📍 Line: {}", element.getLineNumber());
        }

        log.error("📋 Full Stack Trace:");
        log.error(getStackTraceAsString(exception));
        log.error("╚════════════════════════════════════════════════════════════════╝");

        ErrorDetailResponse error = new ErrorDetailResponse(
                Instant.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Null Pointer Error",
                "A null value was encountered unexpectedly. Check logs for details.",
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /**
     * Handle IllegalArgumentException
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorDetailResponse> handleIllegalArgumentException(
            IllegalArgumentException exception,
            HttpServletRequest request) {

        log.error("╔════════════════════════════════════════════════════════════════╗");
        log.error("║        ILLEGAL ARGUMENT EXCEPTION DETECTED                     ║");
        log.error("╚════════════════════════════════════════════════════════════════╝");
        log.error("📍 Endpoint: {} {}", request.getMethod(), request.getRequestURI());
        log.error("🔴 Error: {}", exception.getMessage());
        log.error("📋 Stack Trace:");
        log.error(getStackTraceAsString(exception));
        log.error("╚════════════════════════════════════════════════════════════════╝");

        ErrorDetailResponse error = new ErrorDetailResponse(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Invalid Argument",
                exception.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // ============================================
    // EXISTING HANDLERS (ENHANCED)
    // ============================================

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorDetailResponse> handleNotFoundException(
            NotFoundException exception,
            HttpServletRequest request) {

        log.warn("⚠️ Not Found: {} - URI: {}", exception.getMessage(), request.getRequestURI());

        ErrorDetailResponse error = new ErrorDetailResponse(
                Instant.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                exception.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorDetailResponse> handleBadRequestException(
            BadRequestException exception,
            HttpServletRequest request) {

        log.warn("⚠️ Bad Request: {} - URI: {}", exception.getMessage(), request.getRequestURI());

        ErrorDetailResponse error = new ErrorDetailResponse(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                exception.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorDetailResponse> handleAccessDeniedException(
            AccessDeniedException exception,
            HttpServletRequest request) {

        log.warn("⚠️ Access Denied: {} - URI: {}", exception.getMessage(), request.getRequestURI());

        ErrorDetailResponse error = new ErrorDetailResponse(
                Instant.now(),
                HttpStatus.FORBIDDEN.value(),
                HttpStatus.FORBIDDEN.getReasonPhrase(),
                exception.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.put(error.getField(), error.getDefaultMessage())
        );

        log.warn("⚠️ Validation Failed: {} fields with errors", fieldErrors.size());
        fieldErrors.forEach((field, message) -> log.warn("  - {}: {}", field, message));

        ErrorDetailResponse error = new ErrorDetailResponse(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Validation failed",
                request.getDescription(false).replace("uri=", "")
        );

        Map<String, Object> response = new HashMap<>();
        response.put("error", error);
        response.put("fields", fieldErrors);

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * CATCH-ALL HANDLER FOR UNEXPECTED EXCEPTIONS
     * This will catch any exception not handled by specific handlers above
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetailResponse> handleGlobalException(
            Exception exception,
            HttpServletRequest request) {

        log.error("╔════════════════════════════════════════════════════════════════╗");
        log.error("║          UNEXPECTED EXCEPTION CAUGHT                           ║");
        log.error("╚════════════════════════════════════════════════════════════════╝");
        log.error("📍 Endpoint: {} {}", request.getMethod(), request.getRequestURI());
        log.error("🔴 Exception Type: {}", exception.getClass().getName());
        log.error("🔴 Message: {}", exception.getMessage());

        Throwable rootCause = getRootCause(exception);
        log.error("🎯 Root Cause Type: {}", rootCause.getClass().getName());
        log.error("🎯 Root Cause Message: {}", rootCause.getMessage());

        // Log where the error occurred
        StackTraceElement[] stackTrace = exception.getStackTrace();
        if (stackTrace.length > 0) {
            log.error("📂 Error Location:");
            for (int i = 0; i < Math.min(5, stackTrace.length); i++) {
                StackTraceElement element = stackTrace[i];
                log.error("  {}. {} - {}:{}",
                        i + 1,
                        element.getClassName() + "." + element.getMethodName(),
                        element.getFileName(),
                        element.getLineNumber()
                );
            }
        }

        log.error("📋 Full Stack Trace:");
        log.error(getStackTraceAsString(exception));

        log.error("🧵 Thread: {}", Thread.currentThread().getName());
        log.error("⏰ Timestamp: {}", Instant.now());
        log.error("╚════════════════════════════════════════════════════════════════╝");

        ErrorDetailResponse error = new ErrorDetailResponse(
                Instant.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                exception.getMessage() != null ? exception.getMessage() : "An unexpected error occurred",
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    // ============================================
    // HELPER METHODS - ERROR ANALYSIS
    // ============================================

    /**
     * Get the root cause of an exception
     */
    private Throwable getRootCause(Throwable throwable) {
        Throwable cause = throwable;
        while (cause.getCause() != null && cause.getCause() != cause) {
            cause = cause.getCause();
        }
        return cause;
    }

    /**
     * Convert stack trace to string
     */
    private String getStackTraceAsString(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString();
    }

    /**
     * Diagnose Redis errors
     */
    private String diagnoseRedisError(RedisSystemException ex, Throwable rootCause) {
        String message = ex.getMessage() != null ? ex.getMessage().toLowerCase() : "";
        String rootMessage = rootCause.getMessage() != null ? rootCause.getMessage().toLowerCase() : "";

        if (message.contains("timeout") || rootMessage.contains("timeout")) {
            return "⏰ Redis Timeout - Azure Redis connection too slow. Increase timeout from 30s to 60s in application.yml";
        }

        if (message.contains("connection refused") || rootMessage.contains("connection refused")) {
            return "🔌 Redis Connection Refused - Check if Redis server is running and accessible";
        }

        if (message.contains("serialization") || message.contains("serialize") ||
                rootCause instanceof SerializationException) {
            return "📦 Serialization Error - Cannot cache this object. Convert Entity to DTO before caching";
        }

        if (message.contains("oom") || message.contains("out of memory")) {
            return "💾 Redis Out of Memory - Redis maxmemory exceeded. Clear cache or increase memory";
        }

        if (message.contains("pool") || rootMessage.contains("pool exhausted")) {
            return "🏊 Connection Pool Exhausted - Increase lettuce.pool.max-active to 20";
        }

        if (message.contains("auth") || message.contains("authentication")) {
            return "🔐 Authentication Failed - Check Redis password in application.yml";
        }

        if (message.contains("ssl") || message.contains("tls")) {
            return "🔒 SSL/TLS Error - Check SSL certificate or set ssl.enabled=true for Azure Redis";
        }

        if (message.contains("lazy") || message.contains("could not initialize proxy")) {
            return "🔄 Lazy Loading Error - Entity has uninitialized relationships. Use @Transactional or fetch eagerly";
        }

        return "❓ Unknown Redis Error - Check full stack trace above for details";
    }

    /**
     * Analyze connection failure
     */
    private String analyzeConnectionFailure(RedisConnectionFailureException ex) {
        String message = ex.getMessage() != null ? ex.getMessage().toLowerCase() : "";

        if (message.contains("unable to connect")) {
            return "Cannot connect to Redis server. Check host, port, and network connectivity";
        }

        if (message.contains("timeout")) {
            return "Connection timeout. Redis server may be too slow or unreachable";
        }

        if (message.contains("refused")) {
            return "Connection refused. Redis server may not be running or firewall blocking";
        }

        return "Redis connection failed: " + ex.getMessage();
    }

    /**
     * Analyze serialization error
     */
    private String analyzeSerializationError(SerializationException ex) {
        String message = ex.getMessage() != null ? ex.getMessage().toLowerCase() : "";

        if (message.contains("cannot deserialize")) {
            return "Cannot deserialize cached data. Cache format may have changed. Clear cache";
        }

        if (message.contains("cannot serialize")) {
            return "Cannot serialize object to Redis. Use DTO instead of Entity for caching";
        }

        if (message.contains("class not found")) {
            return "Cached class not found. Cache may contain old data. Clear cache";
        }

        return "Serialization error: " + ex.getMessage();
    }

    /**
     * Analyze database error
     */
    private String analyzeDatabaseError(DataAccessException ex) {
        String message = ex.getMessage() != null ? ex.getMessage().toLowerCase() : "";

        if (message.contains("connection")) {
            return "Database connection error. Check datasource configuration";
        }

        if (message.contains("timeout")) {
            return "Database query timeout. Query may be too slow or database overloaded";
        }

        if (message.contains("constraint")) {
            return "Database constraint violation. Check for duplicate keys or foreign key violations";
        }

        if (message.contains("syntax")) {
            return "SQL syntax error. Check generated query";
        }

        return "Database error: " + ex.getMessage();
    }
}