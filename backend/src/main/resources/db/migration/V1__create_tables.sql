CREATE TABLE favourites
(
    id       BIGSERIAL PRIMARY KEY,
    symbol   VARCHAR(255) UNIQUE NOT NULL,
    name     VARCHAR(255),
    currency VARCHAR(10)
);

CREATE TABLE histories
(
    id           BIGSERIAL PRIMARY KEY,
    timestamp    TIMESTAMP,
    open_price   DECIMAL(18, 6),
    high_price   DECIMAL(18, 6),
    low_price    DECIMAL(18, 6),
    close_price  DECIMAL(18, 6),
    fav_stock_id BIGINT,
    CONSTRAINT fk_histories_fav_stock FOREIGN KEY (fav_stock_id) REFERENCES favourites (id) ON DELETE CASCADE
);

CREATE TABLE investments
(
    id                  BIGSERIAL PRIMARY KEY,
    identification_code UUID UNIQUE,
    symbol              VARCHAR(255),
    invested_capital    DECIMAL(18, 6),
    buy_date            TIMESTAMP,
    buy_price           DECIMAL(18, 6),
    currency            VARCHAR(10)
);