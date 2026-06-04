CREATE TABLE auction (
    id                  BIGSERIAL PRIMARY KEY,
    seller_id           BIGINT                   NOT NULL,
    title               TEXT                     NOT NULL,
    description         TEXT,
    starting_price      NUMERIC(19, 4)           NOT NULL,
    current_highest_bid NUMERIC(19, 4),
    status              VARCHAR(10)              NOT NULL CHECK (status IN ('OPEN', 'CLOSED')),
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL,
    closes_at           TIMESTAMP WITH TIME ZONE NOT NULL,
    version             BIGINT                   NOT NULL DEFAULT 0
);

CREATE TABLE auction_image_urls (
    auction_id  BIGINT NOT NULL REFERENCES auction (id),
    image_urls  TEXT   NOT NULL
);