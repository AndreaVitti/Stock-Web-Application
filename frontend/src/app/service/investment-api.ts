import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { PostInvestReq } from '../model/post-invest-req';
import { map, Observable } from 'rxjs';
import { Investment } from '../model/investment';
import { ApiRes } from '../model/api-res';

@Injectable({
  providedIn: 'root',
})
export class InvestmentApi {
  http = inject(HttpClient);

  private baseUrl = 'http://localhost:8081/api/v1/investment';

  postInvestment(uri: string, postInvestReq: PostInvestReq): Observable<ApiRes> {
    return this.http.post<ApiRes>(this.baseUrl + uri, postInvestReq);
  }

  getAllInvestments(uri: string): Observable<Investment[]> {
    return this.http.get<ApiRes>(this.baseUrl + uri).pipe(map(res => res.investmentDTOS));
  }

  deleteInvestment(uri: string): Observable<ApiRes> {
    return this.http.delete<ApiRes>(this.baseUrl + uri);
  }

  getInvestment(uri: string): Observable<Investment[]> {
    return this.http.get<ApiRes>(this.baseUrl + uri).pipe(map(res => res.investmentDTOS));
  }
}
