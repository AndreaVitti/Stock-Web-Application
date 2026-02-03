CREATE TABLE fav_stocks
(
    id       BIGSERIAL PRIMARY KEY,
    symbol   VARCHAR(255) UNIQUE NOT NULL,
    name     VARCHAR(255),
    currency VARCHAR(10)
);

CREATE TABLE history_quotes
(
    id       BIGSERIAL PRIMARY KEY,
    date     TIMESTAMP,
    open     DECIMAL(18, 6),
    high     DECIMAL(18, 6),
    low      DECIMAL(18, 6),
    close    DECIMAL(18, 6),
    stock_id BIGINT,
    CONSTRAINT fk_history_stocks FOREIGN KEY (stock_id) REFERENCES fav_stocks (id) ON DELETE CASCADE
);

CREATE TABLE investments
(
    id                  BIGSERIAL PRIMARY KEY,
    identification_code VARCHAR(255) UNIQUE NOT NULL,
    symbol              VARCHAR(255),
    capital             DECIMAL(18, 6),
    init_date           TIMESTAMP,
    init_price          DECIMAL(18, 6),
    currency            VARCHAR(10)
);