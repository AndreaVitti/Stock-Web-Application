import { ChangeDetectorRef, Component, ElementRef, inject, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { StockApi } from '../../service/stock-api';
import { CommonModule } from '@angular/common';
import { concatMap, EMPTY, finalize, map, Observable, Subject, switchMap, tap } from 'rxjs';
import { HistoryChart } from '../history-chart/history-chart';
import { FavButton } from '../fav-button/fav-button';
import { HomeButton } from '../home-button/home-button';
import { SearchBar } from '../search-bar/search-bar';
import { Chart } from 'chart.js';
import { PriceData } from '../../model/price-data';
import { PostFavReq } from '../../model/post-fav-req';
import { InvestModal } from '../invest-modal/invest-modal';
import { InvButtton } from '../inv-buttton/inv-buttton';
import { CurrInvRow } from '../../model/curr-inv-row';

@Component({
  selector: 'app-search-results',
  imports: [CommonModule, HistoryChart, FavButton, HomeButton, SearchBar, InvestModal, InvButtton],
  templateUrl: './search-results.html',
  styleUrl: './search-results.css',
})
export class SearchResults implements OnInit {
  destroy$ = new Subject<void>();
  query = '';
  resSymbol: string = '';
  resLongName: string = '';
  resCurrentPrice = 0;
  resCurrency: string = '';
  resPercentage = 0;
  posOrNeg: boolean = false;
  favouriteOrNot: boolean = false;
  loading: boolean = true;
  currentDate = new Date();
  pricesData: (PriceData)[] = [];
  quoteDate: string[] = [];
  chart!: Chart;
  visibleChart: boolean = false;
  visibleModal: boolean = false;
  firstActivation: boolean = true;
  postFavReq!: PostFavReq;
  fav$: Observable<string> = EMPTY;
  currInvRows: CurrInvRow[] = [];
  totalCapital: number = 0;
  totalGainLoss: number = 0;

  chgdet = inject(ChangeDetectorRef);
  route = inject(ActivatedRoute);
  router = inject(Router);
  stockApi = inject(StockApi);

  @ViewChild(HistoryChart) historyChart!: HistoryChart;
  @ViewChild('chartContExt') chartContExtEle!: ElementRef<HTMLDivElement>;

  ngOnInit() {
    this.route.queryParams.pipe(
      tap(params => {
        this.query = params['q'] || '';
        this.loading = true;
      }),
      switchMap(() => this.stockApi.getStockApi('/get/price/' + this.query).pipe(
        tap(res => {
          this.resSymbol = res.symbol;
          this.resLongName = res.longName
          this.resCurrentPrice = Number(res.price.toFixed(2));
          this.resCurrency = res.currency;
          this.resPercentage = res.percentage;
          if (this.resPercentage >= 0) {
            this.posOrNeg = true;
          } else {
            this.posOrNeg = false;
          }
        }),
        finalize(() => {
          this.firstActivation = true;
          this.visibleChart = false;
          this.pricesData = [];
          this.quoteDate = [];
          this.currInvRows = [];
          if (this.chart) {
            this.chart.destroy();
          }
        }), concatMap(() => this.stockApi.getInvestment('/investment/get/' + this.query).pipe(
          (map(res => this.currInvRows = res.map(res => ({
            idCode: res.idCode,
            capital: res.capital,
            buyPrice: res.buyPrice,
            buyDate: res.date.split('T')[0] + ' ' + res.date.split('T')[1].split('.')[0],
            gainLoss: Number(((((this.resCurrentPrice * 100) / res.buyPrice - 100) / 100) * res.capital).toFixed(2))
          })))),
          concatMap(() => this.stockApi.getFavourite('/favourite/get/' + this.query).pipe(
            (tap(res => {
              if (res !== 'Not found') {
                this.favouriteOrNot = true;
              } else {
                this.favouriteOrNot = false;
              }
            })),
            finalize(() => {
              this.totalCapital = 0;
              this.totalGainLoss = 0;
              this.currInvRows.map(currInvRow => { this.totalCapital += currInvRow.capital });
              this.currInvRows.map(currInvRow => { this.totalGainLoss += currInvRow.gainLoss });
              this.chgdet.markForCheck();
              this.loading = false;
            })
          ))))
      ))
    ).subscribe();
  }

  getHistory() {
    if (this.firstActivation) {
      this.stockApi.getHistoryApi('/get/history/' + this.query).pipe(
        map(quotes => this.pricesData = quotes.map(quote => ({
          y: quote.close,
          x: quote.timestamp.split('T')[0],
          open: quote.open,
          high: quote.high,
          low: quote.low
        })))
      ).subscribe({
        complete: () => {
          this.firstActivation = false;
          this.visibleChart = true;
          this.chart = this.historyChart.chartBuild(this.pricesData, this.posOrNeg);
          this.chgdet.markForCheck();
        }
      });
    } else {
      if (this.visibleChart) {
        this.visibleChart = false;
      } else {
        this.visibleChart = true;
      }
    }
  }

  openInvestmentModal() {
    this.visibleModal = true;
  }

  addToFavourite() {
    this.stockApi.postFavourite('/favourite/add', this.postFavReq = { symbol: this.query }).subscribe();
    this.favouriteOrNot = true;
  }

  refreshTable() {
    this.currInvRows = [];
    this.stockApi.getInvestment('/investment/get/' + this.resSymbol).pipe(
      (map(res => this.currInvRows = res.map(res => ({
        idCode: res.idCode,
        capital: res.capital,
        buyPrice: res.buyPrice,
        buyDate: res.date.split('T')[0] + ' ' + res.date.split('T')[1].split('.')[0],
        gainLoss: Number(((((this.resCurrentPrice * 100) / res.buyPrice - 100) / 100) * res.capital).toFixed(2))
      }))))).subscribe({
        complete: () => {
          this.totalCapital = 0;
          this.totalGainLoss = 0;
          this.currInvRows.map(currInvRow => { this.totalCapital += currInvRow.capital });
          this.currInvRows.map(currInvRow => { this.totalGainLoss += currInvRow.gainLoss });
          this.chgdet.markForCheck();
        }
      });
  }
}