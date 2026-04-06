import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { tap } from 'rxjs';

@Component({
  selector: 'app-error-page',
  imports: [],
  templateUrl: './error-page.html',
  styleUrl: './error-page.css',
})
export class ErrorPage implements OnInit {
  currentRoute = inject(ActivatedRoute);

  query = signal<string>('');
  message = signal<string>('');

  ngOnInit(): void {
    this.currentRoute.queryParams.pipe(tap(params => {
      this.query.set(params['HttpStatusCode']);
      switch (this.query()) {
        case '404':
          this.message.set('NOT FOUND');
          break;
        case '500':
          this.message.set('SERVER ERROR');
          break;
        default:
          this.message.set('UNKNOWN ERROR');
      }
    })).subscribe();
  }
}
