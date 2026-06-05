package bidnest.auction;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@Service
public class AuctionService {

    private final AuctionRepository auctionRepository;

    public AuctionService(AuctionRepository auctionRepository) {
        this.auctionRepository = auctionRepository;
    }

    // Creates a new auction from the request, opens it immediately, and persists it
    public AuctionResponse createAuction(CreateAuctionRequest request) {
        Auction auction = new Auction();
        auction.setSellerId(request.getSellerId());
        auction.setTitle(request.getTitle());
        auction.setDescription(request.getDescription());
        auction.setStartingPrice(request.getStartingPrice());
        auction.setClosesAt(request.getClosesAt());
        auction.setStatus(AuctionStatus.OPEN);
        auction.setCreatedAt(Instant.now());

        return toResponse(auctionRepository.save(auction));
    }

    // Fetches a single auction by ID; 404 if not found
    public AuctionResponse getAuction(Long id) {
        Auction auction = auctionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Auction not found"));
        return toResponse(auction);
    }

    // Returns all currently open auctions for the browse feed
    public List<AuctionResponse> getOpenAuctions() {
        return auctionRepository.findByStatus(AuctionStatus.OPEN)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private AuctionResponse toResponse(Auction auction) {
        AuctionResponse response = new AuctionResponse();
        response.setId(auction.getId());
        response.setSellerId(auction.getSellerId());
        response.setTitle(auction.getTitle());
        response.setDescription(auction.getDescription());
        response.setStartingPrice(auction.getStartingPrice());
        response.setCurrentHighestBid(auction.getCurrentHighestBid());
        response.setStatus(auction.getStatus());
        response.setCreatedAt(auction.getCreatedAt());
        response.setClosesAt(auction.getClosesAt());
        return response;
    }
}