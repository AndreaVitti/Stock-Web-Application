import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { catchError, EMPTY, map, Observable } from 'rxjs';
import { ApiRes } from '../model/api-res';
import { CurrentStatus } from '../model/current-status';
import { Router } from '@angular/router';
import { History } from '../model/history';

@Injectable({
  providedIn: 'root',
})
export class StockApi {
  http = inject(HttpClient);
  router = inject(Router);

  private baseUrl = 'http://localhost:8081/api/v1/stock';

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

  getHistoryApi(uri: string): Observable<History[]> {
    return this.http.get<ApiRes>(this.baseUrl + uri).pipe(
      map(res => res.historyDTOS),
      catchError(err => {
        this.router.navigate(['/errorPage'],
          {
            queryParams: { HttpStatusCode: err.status || 'Unknown error' },
            replaceUrl: true
          });
        return EMPTY;
      }));
  }
}
