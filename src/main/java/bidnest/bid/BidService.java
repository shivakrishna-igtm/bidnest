package bidnest.bid;

import bidnest.auction.Auction;
import bidnest.auction.AuctionRepository;
import bidnest.auction.AuctionStatus;
import org.springframework.http.HttpStatus;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
public class BidService {

    private final BidRepository bidRepository;
    private final AuctionRepository auctionRepository;
    private final AuctionBidUpdater auctionBidUpdater;

    public BidService(BidRepository bidRepository, AuctionRepository auctionRepository, AuctionBidUpdater auctionBidUpdater) {
        this.bidRepository = bidRepository;
        this.auctionRepository = auctionRepository;
        this.auctionBidUpdater = auctionBidUpdater;
    }

    public BidResponse placeBid(PlaceBidRequest request) {
        Auction auction = auctionRepository.findById(request.getAuctionId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Auction not found"));

        if (auction.getStatus() != AuctionStatus.OPEN) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Auction is not open");
        }

        if (!auction.getClosesAt().isAfter(Instant.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Auction has expired");
        }

        BigDecimal floor = auction.getCurrentHighestBid() != null
                ? auction.getCurrentHighestBid()
                : auction.getStartingPrice();

        if (request.getBidAmount().compareTo(floor) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bid must be higher than current highest bid");
        }

        try {
            Bid bid = auctionBidUpdater.executeFullBidTransaction(auction, request.getBidderId(), request.getBidAmount());
            return toResponse(bid);
        } catch (ObjectOptimisticLockingFailureException e) {
            // The transaction rolled back — create a rejected bid record in its own transaction
            Bid rejected = new Bid();
            rejected.setAuction(auction);
            rejected.setBidderId(request.getBidderId());
            rejected.setBidAmount(request.getBidAmount());
            rejected.setPlacedAt(Instant.now());
            auctionBidUpdater.saveRejectedBid(rejected);
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "You were outbid by another bidder — please try again with a higher amount");
        }
    }

    public BidResponse getBid(Long id) {
        Bid bid = bidRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bid not found"));
        return toResponse(bid);
    }

    public List<BidResponse> getBidsForAuction(Long auctionId) {
        return bidRepository.findByAuctionId(auctionId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private BidResponse toResponse(Bid bid) {
        BidResponse response = new BidResponse();
        response.setId(bid.getId());
        response.setAuctionId(bid.getAuction().getId());
        response.setBidderId(bid.getBidderId());
        response.setBidAmount(bid.getBidAmount());
        response.setPlacedAt(bid.getPlacedAt());
        response.setStatus(bid.getStatus());
        return response;
    }
}