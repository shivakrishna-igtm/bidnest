package bidnest.auction;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuctionRepository extends JpaRepository<Auction, Long> {

    // Returns all auctions with the given status, e.g. all OPEN auctions for the browse feed
    List<Auction> findByStatus(AuctionStatus status);

    // Returns all auctions created by a specific seller, e.g. for a seller's dashboard
    List<Auction> findBySellerId(Long sellerId);
}
