import { Component, inject, OnInit, signal } from '@angular/core';
import { HomeButton } from '../home-button/home-button';
import { SearchBar } from '../search-bar/search-bar';
import { FavButton } from '../fav-button/fav-button';
import { InvRow } from '../../model/inv-row';
import { map, tap } from 'rxjs';
import { Router } from '@angular/router';
import { InvestmentApi } from '../../service/investment-api';

@Component({
  selector: 'app-investments',
  imports: [HomeButton, SearchBar, FavButton],
  templateUrl: './investments.html',
  styleUrl: './investments.css',
})
export class Investments implements OnInit {
  investmentService = inject(InvestmentApi);
  router = inject(Router);

  invRows = signal<InvRow[]>([]);

  ngOnInit(): void {
    let invRowsTemp: InvRow[] = [];
    this.investmentService.getAllInvestments('/get/all').pipe(
      map(
        res => invRowsTemp = res.map(res => ({
          identificationCode: res.identificationCode,
          investedCapital: res.investedCapital,
          buyPrice: res.buyPrice,
          buyDate: res.buyDate.split('T')[0] + ' ' + res.buyDate.split('T')[1].split('.')[0],
          symbol: res.symbol,
          currency: res.currency
        }))
      )
    ).subscribe(() => this.invRows.set(invRowsTemp));
  }

  viewInv(symbol: string): void {
    this.router.navigate(['/search'], {
      queryParams: { q: symbol }
    });
  }

  delInv(index: number, idCode: string): void {
    this.investmentService.deleteInvestment('/delete/' + idCode).pipe(
      tap(() => this.invRows.update(rows => rows.filter(row => rows.indexOf(row) !== index)))
    ).subscribe();
  }
}
