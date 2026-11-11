package com.wssoftwares.ikutibnb_backend;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class IkutibnbBackendApplication {

    public static void main(String[] args) {
        // Load .env file only if it exists (for local development)
        // In Docker/production, use environment variables directly
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing() 
                .load();
        
        // Set system properties, fallback to environment variables if .env doesn't exist
        System.setProperty("DB_URL", getEnvOrDefault(dotenv, "DB_URL", System.getenv("DB_URL")));
        System.setProperty("DB_USERNAME", getEnvOrDefault(dotenv, "DB_USERNAME", System.getenv("DB_USERNAME")));
        System.setProperty("DB_PASSWORD", getEnvOrDefault(dotenv, "DB_PASSWORD", System.getenv("DB_PASSWORD")));
        
        System.setProperty("JWT_SECRET", getEnvOrDefault(dotenv, "JWT_SECRET", System.getenv("JWT_SECRET")));

        System.setProperty("APP_BASE_URL", getEnvOrDefault(dotenv, "APP_BASE_URL", System.getenv("APP_BASE_URL")));

        System.setProperty("GOOGLE_CLIENT_ID", getEnvOrDefault(dotenv, "GOOGLE_CLIENT_ID", System.getenv("GOOGLE_CLIENT_ID")));
        System.setProperty("GOOGLE_CLIENT_SECRET", getEnvOrDefault(dotenv, "GOOGLE_CLIENT_SECRET", System.getenv("GOOGLE_CLIENT_SECRET")));

        System.setProperty("MAIL_HOST", getEnvOrDefault(dotenv, "MAIL_HOST", System.getenv("MAIL_HOST")));
        System.setProperty("MAIL_PORT", getEnvOrDefault(dotenv, "MAIL_PORT", System.getenv("MAIL_PORT")));
        System.setProperty("MAIL_USERNAME", getEnvOrDefault(dotenv, "MAIL_USERNAME", System.getenv("MAIL_USERNAME")));
        System.setProperty("MAIL_PASSWORD", getEnvOrDefault(dotenv, "MAIL_PASSWORD", System.getenv("MAIL_PASSWORD")));
        System.setProperty("MAIL_ENCODING", getEnvOrDefault(dotenv, "MAIL_ENCODING", System.getenv("MAIL_ENCODING")));

        // SMS Configuration
        System.setProperty("SMS_PROVIDER", getEnvOrDefault(dotenv, "SMS_PROVIDER", System.getenv("SMS_PROVIDER")));
        System.setProperty("SMS_ACCOUNT_SID", getEnvOrDefault(dotenv, "SMS_ACCOUNT_SID", System.getenv("SMS_ACCOUNT_SID")));
        System.setProperty("SMS_AUTH_TOKEN", getEnvOrDefault(dotenv, "SMS_AUTH_TOKEN", System.getenv("SMS_AUTH_TOKEN")));
        System.setProperty("SMS_FROM_NUMBER", getEnvOrDefault(dotenv, "SMS_FROM_NUMBER", System.getenv("SMS_FROM_NUMBER")));

        // NextSMS Configuration
        System.setProperty("NEXTSMS_API_KEY", getEnvOrDefault(dotenv, "NEXTSMS_API_KEY", System.getenv("NEXTSMS_API_KEY")));
        System.setProperty("NEXTSMS_API_SECRET", getEnvOrDefault(dotenv, "NEXTSMS_API_SECRET", System.getenv("NEXTSMS_API_SECRET")));
        System.setProperty("NEXTSMS_ENDPOINT", getEnvOrDefault(dotenv, "NEXTSMS_ENDPOINT", System.getenv("NEXTSMS_ENDPOINT")));
        System.setProperty("NEXTSMS_FROM_NAME", getEnvOrDefault(dotenv, "NEXTSMS_FROM_NAME", System.getenv("NEXTSMS_FROM_NAME")));

        SpringApplication.run(IkutibnbBackendApplication.class, args);
    }
  
    private static String getEnvOrDefault(Dotenv dotenv, String key, String defaultValue) {
        String value = dotenv.get(key);
        if (value != null && !value.isEmpty()) {
            return value;
        }
        return (defaultValue != null) ? defaultValue : "";
    }
}