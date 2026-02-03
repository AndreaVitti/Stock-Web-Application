import { Component, EventEmitter, inject, Input, Output } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { StockApi } from '../../service/stock-api';
import { PostInvestReq } from '../../model/post-invest-req';

@Component({
  selector: 'app-invest-modal',
  imports: [ReactiveFormsModule],
  templateUrl: './invest-modal.html',
  styleUrl: './invest-modal.css',
})
export class InvestModal {
  investModalForm = new FormGroup({
    capital: new FormControl(''),
    buyPrice: new FormControl(''),
    buyDate: new FormControl('')
  })
  @Input() symbol: string = '';
  @Input() currency: string = '';
  postInvReq!: PostInvestReq;
  @Output() closeModal = new EventEmitter<void>();
  @Output() refreshTable = new EventEmitter<void>();

  stockApi = inject(StockApi);

  addInvest() {
    const capital = this.investModalForm.getRawValue().capital;
    const buyPrice = this.investModalForm.getRawValue().buyPrice;
    const buyDate = this.investModalForm.getRawValue().buyDate;
    this.stockApi.postInvestment('/investment/add', this.postInvReq = {
      symbol: this.symbol,
      capital: (capital === '') ? null : capital,
      initDate: (buyDate === '') ? null : buyDate,
      initPrice: (buyPrice === '') ? null : buyPrice,
      currency: this.currency
    }).subscribe({
      complete: () => {
        this.refreshTable.emit();
        this.closeModal.emit();
      }
    });
  }

  close() {
    this.closeModal.emit();
  }
}
