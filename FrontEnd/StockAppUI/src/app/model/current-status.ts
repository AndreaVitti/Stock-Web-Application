import Decimal from "decimal.js";

export interface CurrentStatus {
    longName: string;
    symbol: string;
    price: number;
    currency: string;
    percentage: number;
}
