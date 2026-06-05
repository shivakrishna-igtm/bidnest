package bidnest.bid;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BidRepository extends JpaRepository<Bid, Long> {

    // Returns all bids placed on a specific auction, used when loading auction bid history
    List<Bid> findByAuctionId(Long auctionId);

    // Returns all bids placed by a specific bidder, newest first, for a bidder's activity feed
    List<Bid> findByBidderIdOrderByPlacedAtDesc(Long bidderId);
}
