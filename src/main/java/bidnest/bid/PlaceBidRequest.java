package bidnest.bid;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PlaceBidRequest {
    private Long auctionId;
    private Long bidderId;
    private BigDecimal bidAmount;
}