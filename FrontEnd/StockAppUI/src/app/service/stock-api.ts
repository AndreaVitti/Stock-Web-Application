import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { catchError, EMPTY, map, Observable, of, throwError } from 'rxjs';
import { Quote } from '../model/quote';
import { ApiRes } from '../model/api-res';
import { CurrentStatus } from '../model/current-status';
import { Stock } from '../model/stock';
import { Router } from '@angular/router';
import { PostFavReq } from '../model/post-fav-req';
import { PostInvestReq } from '../model/post-invest-req';
import { Investment } from '../model/investment';
import { EmailValidationError } from '@angular/forms/signals';

@Injectable({
  providedIn: 'root',
})
export class StockApi {
  private baseUrl = 'http://localhost:8081/stocks';

  http = inject(HttpClient);
  router = inject(Router);

  getStockApi(uri: string): Observable<CurrentStatus> {
    return this.http.get<ApiRes>(this.baseUrl + uri).pipe(
      catchError(err => {
        this.router.navigate(['/errorPage'],
          {
            queryParams: { HttpStatusCode: err.status || 'Unknown error' },
            replaceUrl: true
          });
        return EMPTY;
      }));
  }

  getHistoryApi(uri: string): Observable<Quote[]> {
    return this.http.get<ApiRes>(this.baseUrl + uri).pipe(
      map(res => res.historyQuoteDTO),
      catchError(err => {
        this.router.navigate(['/errorPage'],
          {
            queryParams: { HttpStatusCode: err.status || 'Unknown error' },
            replaceUrl: true
          });
        return EMPTY;
      }));
  }

  postInvestment(uri: string, postInvestReq: PostInvestReq) {
    return this.http.post(this.baseUrl + uri, postInvestReq);
  }

  getAllInvestments(uri: string): Observable<Investment[]> {
    return this.http.get<ApiRes>(this.baseUrl + uri).pipe(map(res => res.investmentDTOS));
  }

  deleteInvestment(uri: string) {
    return this.http.delete(this.baseUrl + uri);
  }

  getInvestment(uri: string) {
    return this.http.get<ApiRes>(this.baseUrl + uri).pipe(map(res => res.investmentDTOS));
  }

  postFavourite(uri: string, postFavReq: PostFavReq) {
    return this.http.post(this.baseUrl + uri, postFavReq);
  }

  getAllFavourites(uri: string): Observable<Stock[]> {
    return this.http.get<ApiRes>(this.baseUrl + uri).pipe(map(res => res.stockDTOS));
  }

  getFavourite(uri: string): Observable<string> {
    return this.http.get<ApiRes>(this.baseUrl + uri).pipe(
      map(res => res.symbol),
      catchError(err => {
        if (err.status = 404) { 
          return of('Not found');
        } else {
          return EMPTY;
        };
      })
    );
  }

  deleteFavourite(uri: string) {
    return this.http.delete(this.baseUrl + uri);
  }

  refreshFavourite(uri: string) {
    return this.http.patch(this.baseUrl + uri, null).pipe(
      catchError(err => {
        if (err.status = 404) {
          return of('Refresh not available')
        }
        return EMPTY;
      })
    );
  }
}
