package bidnest.events;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class BidEventPublisher {

    private final KafkaTemplate<String, BidAcceptedEvent> kafkaTemplate;

    public BidEventPublisher(KafkaTemplate<String, BidAcceptedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishBidAccepted(BidAcceptedEvent event) {
        kafkaTemplate.send("bid-accepted", String.valueOf(event.auctionId()), event);
    }
}