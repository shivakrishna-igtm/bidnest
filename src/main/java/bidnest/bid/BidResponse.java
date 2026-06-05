package bidnest.bid;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class BidResponse {
    private Long id;
    private Long auctionId;
    private Long bidderId;
    private BigDecimal bidAmount;
    private Instant placedAt;
    private BidStatus status;
}