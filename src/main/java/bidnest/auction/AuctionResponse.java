package bidnest.auction;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class AuctionResponse {
    private Long id;
    private Long sellerId;
    private String title;
    private String description;
    private BigDecimal startingPrice;
    private BigDecimal currentHighestBid;
    private AuctionStatus status;
    private Instant createdAt;
    private Instant closesAt;
}