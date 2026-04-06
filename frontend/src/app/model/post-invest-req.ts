export interface PostInvestReq {
    symbol: string;
    investedCapital: string | null;
    buyDate: string | null;
    buyPrice: string | null;
    currency: string;
}
