package com.example.maintenance.notification.application;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@ConditionalOnProperty(prefix="app.notifications.email",name="enabled",havingValue="true")
public class ComplaintNotificationListener {
    private final ComplaintNotificationService notifications;
    public ComplaintNotificationListener(ComplaintNotificationService notifications){this.notifications=notifications;}

    @Async("notificationExecutor")
    @TransactionalEventListener(phase=TransactionPhase.AFTER_COMMIT)
    public void onComplaintSubmitted(ComplaintSubmittedEvent event){notifications.sendComplaintSubmitted(event.complaintId());}
}
