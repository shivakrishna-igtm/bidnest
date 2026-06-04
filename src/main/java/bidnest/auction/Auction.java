package bidnest.auction;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Auction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long sellerId;

    private String title;

    private String description;

    @ElementCollection
    private List<String> imageUrls;

    private BigDecimal startingPrice;

    // Mutated on every accepted bid; read by bid validation to enforce minimums
    private BigDecimal currentHighestBid;

    @Enumerated(EnumType.STRING)
    private AuctionStatus status;

    private Instant createdAt;

    private Instant closesAt;

    // Incremented by the DB on every update; prevents lost-update races between concurrent bids
    @Version
    private Long version;
}