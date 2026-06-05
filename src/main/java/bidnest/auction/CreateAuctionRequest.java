package bidnest.auction;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class CreateAuctionRequest {
    private Long sellerId;
    private String title;
    private String description;
    private BigDecimal startingPrice;
    private Instant closesAt;
}