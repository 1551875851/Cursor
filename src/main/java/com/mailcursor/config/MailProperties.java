package com.mailcursor.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "mail")
public class MailProperties {

    /** Allowed recipient address (only this mailbox can receive mail). */
    private String allowedRecipient;

    public String getAllowedRecipient() {
        return allowedRecipient;
    }

    public void setAllowedRecipient(String allowedRecipient) {
        this.allowedRecipient = allowedRecipient;
    }
}
