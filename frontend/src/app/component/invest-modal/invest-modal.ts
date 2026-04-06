import { Component, EventEmitter, inject, Input, Output } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { InvestmentApi } from '../../service/investment-api';

@Component({
  selector: 'app-invest-modal',
  imports: [ReactiveFormsModule],
  templateUrl: './invest-modal.html',
  styleUrl: './invest-modal.css',
})
export class InvestModal {
  investmentService = inject(InvestmentApi);

  investModalForm = new FormGroup({
    capital: new FormControl(''),
    buyPrice: new FormControl(''),
    buyDate: new FormControl('')
  });

  @Input() symbol: string = '';
  @Input() currency: string = '';
  @Output() closeModal = new EventEmitter<void>();
  @Output() refreshTable = new EventEmitter<void>();

  addInvest(): void {
    const capital = this.investModalForm.value?.capital;
    const buyPrice = this.investModalForm.value?.buyPrice;
    const buyDate = this.investModalForm.value?.buyDate;
    this.investmentService.postInvestment('/add', {
      symbol: this.symbol,
      investedCapital: (!capital) ? null : capital,
      buyDate: (!buyDate) ? null : buyDate,
      buyPrice: (!buyPrice) ? null : buyPrice,
      currency: this.currency
    }).subscribe(() => {
      this.refreshTable.emit();
      this.closeModal.emit();
    });
  }

  close(): void {
    this.closeModal.emit();
  }
}
