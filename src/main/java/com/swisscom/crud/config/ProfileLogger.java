package com.swisscom.crud.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ProfileLogger {
    private static final Logger logger = LoggerFactory.getLogger(ProfileLogger.class);

    @Value("${spring.data.mongodb.uri}")
    private String mongoDbUri;

    @Value("${custom.profileInfo:No specific profile info set}")
    private String profileInfoMessage;

    @PostConstruct
    public void logActiveProfileConfiguration() {
        logger.info("----------------------------------------------------------");
        logger.info("APPLICATION PROFILE ACTIVE");
        logger.info("Custom Info: {}", profileInfoMessage);
        logger.info("MongoDB URI: {}", mongoDbUri);
        logger.info("----------------------------------------------------------");
    }
}