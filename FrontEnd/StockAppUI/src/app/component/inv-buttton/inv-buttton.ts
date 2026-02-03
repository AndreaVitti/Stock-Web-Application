import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-inv-buttton',
  imports: [],
  templateUrl: './inv-buttton.html',
  styleUrl: './inv-buttton.css',
})
export class InvButtton {
  router = inject(Router)

  openInv() {
    this.router.navigate(['/investments'])
   }
}
