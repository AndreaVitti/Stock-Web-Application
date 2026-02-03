import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { map, tap } from 'rxjs';

@Component({
  selector: 'app-error-page',
  imports: [],
  templateUrl: './error-page.html',
  styleUrl: './error-page.css',
})
export class ErrorPage implements OnInit {
  query: string = '';
  message: string = '';

  route = inject(ActivatedRoute);

  ngOnInit(): void {
    this.route.queryParams.pipe(tap(params => {
      this.query = params['HttpStatusCode'];
      switch (this.query) {
        case '404':
          this.message = 'NOT FOUND';
          break;
        case '500':
          this.message = 'SERVER ERROR';
          break;
        default:
          this.message = 'UNKNOWN ERROR';
      }
    })).subscribe();
  }
}
