import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { HomeButton } from '../home-button/home-button';
import { SearchBar } from '../search-bar/search-bar';
import { FavButton } from '../fav-button/fav-button';
import { InvRow } from '../../model/inv-row';
import { StockApi } from '../../service/stock-api';
import { finalize, map, tap } from 'rxjs';
import { Router } from '@angular/router';

@Component({
  selector: 'app-investments',
  imports: [HomeButton, SearchBar, FavButton],
  templateUrl: './investments.html',
  styleUrl: './investments.css',
})
export class Investments implements OnInit {
  invRows: InvRow[] = [];

  stockApi = inject(StockApi);
  chgdet = inject(ChangeDetectorRef);
  router = inject(Router);

  ngOnInit(): void {
    this.stockApi.getAllInvestments('/investment/get/all').pipe(
      map(
        res => this.invRows = res.map(res => ({
          idCode: res.idCode,
          capital: res.capital,
          buyPrice: res.buyPrice,
          buyDate: res.date.split('T')[0] + ' ' + res.date.split('T')[1].split('.')[0],
          symbol: res.symbol,
          currency: res.currency
        })
        )
      ), finalize(() => this.chgdet.markForCheck())
    ).subscribe();
  }

  viewInv(symbol: string) {
    this.router.navigate(['/search'], {
      queryParams: { q: symbol }
    });
  }

  delInv(btn: HTMLButtonElement, idCode: string) {
    this.stockApi.deleteInvestment('/investment/delete/' + idCode).pipe(
      tap(() => btn.parentElement?.parentElement?.remove())
    ).subscribe();
  }
}
