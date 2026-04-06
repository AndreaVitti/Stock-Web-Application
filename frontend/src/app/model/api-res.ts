import { History } from "./history";
import { Investment } from "./investment";
import { Stock } from "./stock";

export interface ApiRes {
    stockDTO: Stock;
    stockDTOS: Stock[];
    historyDTOS: History[];
    investmentDTOS: Investment[];
    currentPrice: number;
    percentage: number;
}
