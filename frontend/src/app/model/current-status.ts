import Decimal from "decimal.js";
import { Stock } from "./stock";

export interface CurrentStatus {
    stockDTO: Stock;
    currentPrice: number;
    percentage: number;
}
