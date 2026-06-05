CREATE TABLE bid (
    id         BIGSERIAL                PRIMARY KEY,
    auction_id BIGINT                   NOT NULL,
    bidder_id  BIGINT                   NOT NULL,
    bid_amount NUMERIC(19, 4)           NOT NULL,
    placed_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    status     VARCHAR(20)              NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'ACCEPTED', 'REJECTED')),
    version    BIGINT                   NOT NULL DEFAULT 0,

    CONSTRAINT fk_bid_auction FOREIGN KEY (auction_id) REFERENCES auction (id)
);

CREATE INDEX idx_bid_auction_id ON bid (auction_id);
