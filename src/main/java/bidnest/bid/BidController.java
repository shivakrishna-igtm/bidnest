package bidnest.bid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bids")
public class BidController {

    private final BidService bidService;

    public BidController(BidService bidService) {
        this.bidService = bidService;
    }

    // Places a new bid; returns 201 with the created bid
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BidResponse placeBid(@RequestBody PlaceBidRequest request) {
        return bidService.placeBid(request);
    }

    // Returns a single bid by ID; 404 if not found
    @GetMapping("/{id}")
    public BidResponse getBid(@PathVariable Long id) {
        return bidService.getBid(id);
    }

    // Returns all bids placed on a specific auction
    @GetMapping("/auction/{auctionId}")
    public List<BidResponse> getBidsForAuction(@PathVariable Long auctionId) {
        return bidService.getBidsForAuction(auctionId);
    }
}