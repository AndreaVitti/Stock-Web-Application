import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { PostFavReq } from '../model/post-fav-req';
import { catchError, EMPTY, map, Observable, of } from 'rxjs';
import { ApiRes } from '../model/api-res';
import { Stock } from '../model/stock';

@Injectable({
  providedIn: 'root',
})
export class FavStockApi {
  http = inject(HttpClient);

  private baseUrl = 'http://localhost:8081/api/v1/favourite';

  postFavourite(uri: string, postFavReq: PostFavReq): Observable<ApiRes> {
    return this.http.post<ApiRes>(this.baseUrl + uri, postFavReq);
  }

  getAllFavourites(uri: string): Observable<Stock[]> {
    return this.http.get<ApiRes>(this.baseUrl + uri).pipe(map(res => res.stockDTOS));
  }

  getFavourite(uri: string): Observable<string> {
    return this.http.get<ApiRes>(this.baseUrl + uri).pipe(
      map(res => res.stockDTO.symbol),
      catchError(err => {
        if (err.status = 404) {
          return of('Not found');
        } else {
          return EMPTY;
        };
      })
    );
  }

  deleteFavourite(uri: string): Observable<ApiRes> {
    return this.http.delete<ApiRes>(this.baseUrl + uri);
  }

  refreshFavourite(uri: string): Observable<ApiRes | string> {
    return this.http.patch<ApiRes>(this.baseUrl + uri, null).pipe(
      catchError(err => {
        if (err.status = 404) {
          return of('Refresh not available')
        }
        return EMPTY;
      })
    );
  }
}
