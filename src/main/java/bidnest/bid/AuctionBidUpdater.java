package bidnest.bid;

import bidnest.auction.Auction;
import bidnest.auction.AuctionRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;

@Component
public class AuctionBidUpdater {

    private final AuctionRepository auctionRepository;
    private final BidRepository bidRepository;

    public AuctionBidUpdater(AuctionRepository auctionRepository, BidRepository bidRepository) {
        this.auctionRepository = auctionRepository;
        this.bidRepository = bidRepository;
    }

    @Transactional
    public Bid executeFullBidTransaction(Auction auction, Long bidderId, BigDecimal bidAmount) {
        Bid bid = new Bid();
        bid.setAuction(auction);
        bid.setBidderId(bidderId);
        bid.setBidAmount(bidAmount);
        bid.setPlacedAt(Instant.now());
        bid.setStatus(BidStatus.PENDING);
        bid = bidRepository.save(bid);

        auction.setCurrentHighestBid(bidAmount);
        auctionRepository.save(auction);

        bid.setStatus(BidStatus.ACCEPTED);
        return bidRepository.save(bid);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Bid saveRejectedBid(Bid bid) {
        bid.setStatus(BidStatus.REJECTED);
        return bidRepository.save(bid);
    }
}