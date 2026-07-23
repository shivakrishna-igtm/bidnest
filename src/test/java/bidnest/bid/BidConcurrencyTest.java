package bidnest.bid;

import bidnest.auction.Auction;
import bidnest.auction.AuctionRepository;
import bidnest.auction.AuctionStatus;
import bidnest.events.BidEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

// Proves that the @Version-based optimistic locking on Auction prevents concurrent bids from
// all succeeding: exactly one bid wins the race and the rest are rejected with a 409 CONFLICT.
@SpringBootTest
@MockitoBean(types = BidEventPublisher.class)
class BidConcurrencyTest {

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private BidService bidService;

    private Long auctionId;

    @BeforeEach
    void setUp() {
        // Clean state before each run — bids first to satisfy the FK constraint
        bidRepository.deleteAll();
        auctionRepository.deleteAll();

        Auction auction = new Auction();
        auction.setSellerId(1L);
        auction.setTitle("Test Auction");
        auction.setDescription("Concurrency test auction");
        auction.setStartingPrice(new BigDecimal("100.00"));
        auction.setStatus(AuctionStatus.OPEN);
        auction.setCreatedAt(Instant.now());
        auction.setClosesAt(Instant.now().plusSeconds(3600));

        auctionId = auctionRepository.save(auction).getId();
    }

    @Test
    void whenMultipleBidsArePlacedSimultaneously_exactlyOneIsAccepted() throws InterruptedException {
        int threadCount = 10;

        // The CountDownLatch acts as a starting gun: each thread parks on latch.await() until
        // countDown() fires, releasing all 10 threads at once to maximise the chance of a race.
        CountDownLatch startingGun = new CountDownLatch(1);

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Runnable> tasks = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            final long bidderId = i + 1L;
            tasks.add(() -> {
                try {
                    startingGun.await(); // park until all threads are ready
                    PlaceBidRequest request = new PlaceBidRequest();
                    request.setAuctionId(auctionId);
                    request.setBidderId(bidderId);
                    request.setBidAmount(new BigDecimal("150.00"));
                    bidService.placeBid(request);
                } catch (Exception ignored) {
                    // Each losing thread will throw a ResponseStatusException(CONFLICT) or a
                    // transaction rollback exception — both are expected and intentionally swallowed here.
                    // What matters is the final DB state asserted below, not which thread threw.
                }
            });
        }

        tasks.forEach(executor::submit);


        // Release all 10 threads simultaneously to maximise concurrency
        startingGun.countDown();

        executor.shutdown();
        executor.awaitTermination(10, java.util.concurrent.TimeUnit.SECONDS);

        // Exactly one bid must have won the race and been marked ACCEPTED
        long acceptedCount = bidRepository.findAll().stream()
                .filter(b -> b.getStatus() == BidStatus.ACCEPTED)
                .count();

        // All other bids must have been explicitly rejected by the catch block in BidService
        long rejectedCount = bidRepository.findAll().stream()
                .filter(b -> b.getStatus() == BidStatus.REJECTED)
                .count();

        assertEquals(1, acceptedCount, "Exactly one bid should be accepted");
        assertEquals(threadCount - 1, rejectedCount, "All other bids should be rejected");

        // The auction's high-water mark must reflect the single winning bid
        Auction auction = auctionRepository.findById(auctionId).orElseThrow();
        assertNotNull(auction.getCurrentHighestBid());
        assertEquals(0, new BigDecimal("150.00").compareTo(auction.getCurrentHighestBid()),
                "Auction currentHighestBid should equal the winning bid amount");
    }
}





