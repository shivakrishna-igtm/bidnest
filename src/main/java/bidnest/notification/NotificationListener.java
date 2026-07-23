package bidnest.notification;

import bidnest.events.BidAcceptedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationListener {

    private static final Logger log = LoggerFactory.getLogger(NotificationListener.class);

    @KafkaListener(topics = "bid-accepted", groupId = "notification-group")
    public void onBidAccepted(BidAcceptedEvent event) {
        log.info("NOTIFICATION: Bidder {} won auction {} with bid {}",
                event.bidderId(), event.auctionId(), event.bidAmount());
    }
}