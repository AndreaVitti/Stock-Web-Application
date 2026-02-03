import { ChangeDetectorRef, Component, ElementRef, inject, OnInit, ViewChild } from '@angular/core';
import { StockApi } from '../../service/stock-api';
import { Stock } from '../../model/stock';
import { EMPTY, finalize, map, Observable, tap } from 'rxjs';
import { FavRow } from '../../model/fav-row';
import { Router } from '@angular/router';
import { HomeButton } from '../home-button/home-button';
import { SearchBar } from '../search-bar/search-bar';
import { InvButtton } from '../inv-buttton/inv-buttton';

@Component({
  selector: 'app-favourites',
  imports: [HomeButton, SearchBar, InvButtton],
  templateUrl: './favourites.html',
  styleUrl: './favourites.css',
})
export class Favourites implements OnInit {
  favRows: FavRow[] = [];

  router = inject(Router);
  stockApi = inject(StockApi);
  chgdet = inject(ChangeDetectorRef);

  ngOnInit(): void {
    this.stockApi.getAllFavourites('/favourite/get/all').pipe(
      map(res => {
        this.favRows = res.map(res => ({
          symbol: res.symbol,
          name: res.name,
          currency: res.currency
        })
        );
      }),
      finalize(() => this.chgdet.markForCheck())
    ).subscribe();
  }

  viewFav(symbol: string) {
    this.router.navigate(['/search'], {
      queryParams: { q: symbol }
    });
  }

  delFav(btn: HTMLButtonElement, symbol: string) {
    this.stockApi.deleteFavourite('/favourite/delete/' + symbol).pipe(
      tap(() => btn.parentElement?.parentElement?.remove())
    ).subscribe();
  }

  refreshFav(symbol: string) {
    this.stockApi.refreshFavourite('/favourite/refresh/' + symbol).pipe(
      tap(res => {
        if (res === 'Refresh not available'){
          alert(res);
        }
    })).subscribe();
  }
}
