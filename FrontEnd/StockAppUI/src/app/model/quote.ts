import Decimal from "decimal.js";

export interface Quote {
    timestamp: string;
    high: number;
    open: number;
    close: number;
    low: number;
}
