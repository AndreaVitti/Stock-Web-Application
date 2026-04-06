import { Component, ElementRef, inject, OnInit, signal, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { StockApi } from '../../service/stock-api';
import { CommonModule } from '@angular/common';
import { concatMap, finalize, map, Subject, switchMap, tap } from 'rxjs';
import { HistoryChart } from '../history-chart/history-chart';
import { FavButton } from '../fav-button/fav-button';
import { HomeButton } from '../home-button/home-button';
import { SearchBar } from '../search-bar/search-bar';
import { Chart } from 'chart.js';
import { PriceData } from '../../model/price-data';
import { InvestModal } from '../invest-modal/invest-modal';
import { InvButtton } from '../inv-buttton/inv-buttton';
import { CurrInvRow } from '../../model/curr-inv-row';
import { FavStockApi } from '../../service/fav-stock-api';
import { InvestmentApi } from '../../service/investment-api';
import { CurrentStatus } from '../../model/current-status';
import { Investment } from '../../model/investment';

@Component({
  selector: 'app-search-results',
  imports: [CommonModule, HistoryChart, FavButton, HomeButton, SearchBar, InvestModal, InvButtton],
  templateUrl: './search-results.html',
  styleUrl: './search-results.css',
})
export class SearchResults implements OnInit {
  currentRoute = inject(ActivatedRoute);
  stockService = inject(StockApi);
  favService = inject(FavStockApi);
  investmentService = inject(InvestmentApi);

  loading = signal<boolean>(true);
  stockResult = signal<CurrentStatus>({
    stockDTO: {
      name: '',
      symbol: '',
      currency: ''
    },
    currentPrice: 0,
    percentage: 0
  });
  resCurrentPrice = signal<number>(0);
  favouriteOrNot = signal<boolean>(false);
  currentDate = signal<Date>(new Date());
  currInvRows = signal<CurrInvRow[]>([]);
  totalCapital = signal<number>(0);
  totalGainLoss = signal<number>(0);
  visibleChart = signal<boolean>(false);
  visibleModal: boolean = false;
  destroy$ = new Subject<void>();
  private query = '';
  private posOrNeg: boolean = false;
  private pricesData: (PriceData)[] = [];
  private chart!: Chart;
  private firstActivation: boolean = true;

  @ViewChild(HistoryChart) historyChart!: HistoryChart;

  ngOnInit(): void {
    this.currentRoute.queryParams.pipe(
      tap(params => {
        this.query = params['q'] || '';
        this.loading.set(true);
      }),
      switchMap(() => this.stockService.getStockApi('/get/current/' + this.query).pipe(
        tap(res => {
          this.stockResult.set(res);
          this.resCurrentPrice.set(Number(res.currentPrice.toFixed(2)));
          this.stockResult().percentage >= 0 ? this.posOrNeg = true : this.posOrNeg = false;
        }),
        finalize(() => {
          this.firstActivation = true;
          this.visibleChart.set(false);
          this.pricesData = [];
          this.currInvRows.set([]);
          if (this.chart) {
            this.chart.destroy();
          }
        }), concatMap(() => this.investmentService.getInvestment('/get/' + this.query).pipe(
          (map(res => this.currInvRows.set(res.map(res => this.investmentBuilder(res))))),
          concatMap(() => this.favService.getFavourite('/get/' + this.query).pipe(
            (tap(res => {
              res !== 'Not found' ? this.favouriteOrNot.set(true) : this.favouriteOrNot.set(false);
            })),
            finalize(() => {
              this.resetTotals();
              this.loading.set(false);
            })
          ))))
      ))
    ).subscribe();
  }

  getHistory(): void {
    if (this.firstActivation) {
      this.stockService.getHistoryApi('/get/history/' + this.query).pipe(
        map(historyQuotes => this.pricesData = historyQuotes.map(historyQuote => ({
          y: historyQuote.closePrice,
          x: historyQuote.timestamp.split('T')[0],
          open: historyQuote.openPrice,
          high: historyQuote.highPrice,
          low: historyQuote.lowPrice
        })))
      ).subscribe(() => {
        this.firstActivation = false;
        this.visibleChart.set(true);
        this.chart = this.historyChart.chartBuild(this.pricesData, this.posOrNeg, this.currInvRows());
      });
    } else {
      this.visibleChart() ? this.visibleChart.set(false) : this.visibleChart.set(true);
    }
  }

  openInvestmentModal(): void {
    this.visibleModal = true;
  }

  addToFavourite(): void {
    this.favService.postFavourite('/add', { symbol: this.query }).subscribe();
    this.favouriteOrNot.set(true);
  }

  refreshTable(): void {
    this.currInvRows.set([]);
    this.investmentService.getInvestment('/get/' + this.stockResult().stockDTO.symbol).pipe(
      (map(res => this.currInvRows.set(res.map(res =>
        this.investmentBuilder(res)
      ))))).subscribe(() => {
        this.resetTotals();
        this.historyChart.udpateChart(this.currInvRows());
      });
  }

  private investmentBuilder(res: Investment): CurrInvRow {
    return {
      identificationCode: res.identificationCode,
      investedCapital: res.investedCapital,
      buyPrice: res.buyPrice,
      buyDate: res.buyDate.split('T')[0] + ' ' + res.buyDate.split('T')[1].split('.')[0],
      gainLoss: Number((((this.resCurrentPrice() * res.investedCapital) / res.buyPrice) - res.investedCapital).toFixed(2))
    }
  }

  private resetTotals(): void {
    this.totalCapital.set(0);
    this.totalGainLoss.set(0);
    let totalCapitalTemp = 0;
    this.currInvRows().forEach(currInvRow => {
      totalCapitalTemp += currInvRow.investedCapital;
    });
    this.totalCapital.set(Number(totalCapitalTemp.toFixed(2)));
    let totalGainLossTemp = 0;
    this.currInvRows().forEach(currInvRow => {
      totalGainLossTemp += currInvRow.gainLoss;
    });
    this.totalGainLoss.set(Number(totalGainLossTemp.toFixed(2)));
  }
}