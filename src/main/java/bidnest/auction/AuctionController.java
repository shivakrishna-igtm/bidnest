package bidnest.auction;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auctions")
public class AuctionController {

    private final AuctionService auctionService;

    public AuctionController(AuctionService auctionService) {
        this.auctionService = auctionService;
    }

    // Creates a new auction; returns 201 with the created resource
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AuctionResponse createAuction(@RequestBody CreateAuctionRequest request) {
        return auctionService.createAuction(request);
    }

    // Returns a single auction by ID; 404 if not found
    @GetMapping("/{id}")
    public AuctionResponse getAuction(@PathVariable Long id) {
        return auctionService.getAuction(id);
    }

    // Returns all open auctions for the browse feed
    @GetMapping
    public List<AuctionResponse> getOpenAuctions() {
        return auctionService.getOpenAuctions();
    }
}