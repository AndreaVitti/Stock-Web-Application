export interface PostInvestReq {
    symbol: string;
    capital: string | null;
    initDate: string | null;
    initPrice: string | null;
    currency: string;
}
