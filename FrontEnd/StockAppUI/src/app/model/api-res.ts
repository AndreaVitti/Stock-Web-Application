import { Investment } from "./investment";
import { Quote } from "./quote";
import { Stock } from "./stock";

export interface ApiRes {
    stockDTOS: Stock[];
    historyQuoteDTO: Quote[];
    investmentDTOS: Investment[];
    currency: string;
    symbol: string;
    longName: string;
    price: number;
    percentage: number;
}
