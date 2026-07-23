package bidnest.events;

import java.math.BigDecimal;
import java.time.Instant;

public record BidAcceptedEvent(Long auctionId, Long bidId, Long bidderId, BigDecimal bidAmount, Instant occurredAt) {}