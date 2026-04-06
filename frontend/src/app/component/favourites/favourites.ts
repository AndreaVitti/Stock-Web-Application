import { Component, inject, OnInit, signal } from '@angular/core';
import { map, tap } from 'rxjs';
import { FavRow } from '../../model/fav-row';
import { Router } from '@angular/router';
import { HomeButton } from '../home-button/home-button';
import { SearchBar } from '../search-bar/search-bar';
import { InvButtton } from '../inv-buttton/inv-buttton';
import { FavStockApi } from '../../service/fav-stock-api';

@Component({
  selector: 'app-favourites',
  imports: [HomeButton, SearchBar, InvButtton],
  templateUrl: './favourites.html',
  styleUrl: './favourites.css',
})
export class Favourites implements OnInit {
  router = inject(Router);
  favService = inject(FavStockApi);

  favRows = signal<FavRow[]>([]);

  ngOnInit(): void {
    let favRowsTemp: FavRow[] = [];
    this.favService.getAllFavourites('/get/all').pipe(
      map(res => {
        favRowsTemp = res.map(res => ({
          symbol: res.symbol,
          name: res.name,
          currency: res.currency
        })
        );
      })
    ).subscribe(() => this.favRows.set(favRowsTemp));
  }

  viewFav(symbol: string): void {
    this.router.navigate(['/search'], {
      queryParams: { q: symbol }
    });
  }

  delFav(index : number, symbol: string): void {
    this.favService.deleteFavourite('/delete/' + symbol).pipe(
      tap(() => this.favRows.update(rows => rows.filter(row => rows.indexOf(row) !== index)))
    ).subscribe();
  }

  refreshFav(symbol: string): void {
    this.favService.refreshFavourite('/refresh/' + symbol).pipe(
      tap(res => {
        if (res === 'Refresh not available') {
          alert(res);
        }
      })).subscribe();
  }
}
