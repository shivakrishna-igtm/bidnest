package bidnest.bid;

import bidnest.auction.Auction;
import bidnest.auction.AuctionRepository;
import bidnest.auction.AuctionStatus;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
public class BidService {

    private final BidRepository bidRepository;
    private final AuctionRepository auctionRepository;

    public BidService(BidRepository bidRepository, AuctionRepository auctionRepository) {
        this.bidRepository = bidRepository;
        this.auctionRepository = auctionRepository;
    }

    @Transactional
    public BidResponse placeBid(PlaceBidRequest request) {
        // Load the auction — we need it for all subsequent validation and to update currentHighestBid
        Auction auction = auctionRepository.findById(request.getAuctionId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Auction not found"));

        // Reject bids on closed auctions — status is the authoritative gate, checked before time
        if (auction.getStatus() != AuctionStatus.OPEN) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Auction is not open");
        }

        // Reject bids past the closing time — closesAt is a hard deadline regardless of status
        if (!auction.getClosesAt().isAfter(Instant.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Auction has expired");
        }

        // Floor is startingPrice until the first bid lands, then currentHighestBid takes over
        BigDecimal floor = auction.getCurrentHighestBid() != null
                ? auction.getCurrentHighestBid()
                : auction.getStartingPrice();

        // Strictly greater than — equal bids don't displace the current leader
        if (request.getBidAmount().compareTo(floor) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bid must be higher than current highest bid");
        }

        // Persist the bid as PENDING before touching the auction, so we have a record even if something fails
        Bid bid = new Bid();
        bid.setAuction(auction);
        bid.setBidderId(request.getBidderId());
        bid.setBidAmount(request.getBidAmount());
        bid.setPlacedAt(Instant.now());
        bid.setStatus(BidStatus.PENDING);
        bid = bidRepository.save(bid);

        // Advance the auction's high-water mark — @Version on Auction guards against a concurrent bid
        // winning this same write and producing a stale currentHighestBid
        auction.setCurrentHighestBid(request.getBidAmount());
        auctionRepository.save(auction);

        // Mark the bid accepted now that the auction state is consistent
        bid.setStatus(BidStatus.ACCEPTED);
        bid = bidRepository.save(bid);

        return toResponse(bid);
    }

    // Fetches a single bid by ID; 404 if not found
    public BidResponse getBid(Long id) {
        Bid bid = bidRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bid not found"));
        return toResponse(bid);
    }

    // Returns all bids for a given auction, used when loading auction bid history
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