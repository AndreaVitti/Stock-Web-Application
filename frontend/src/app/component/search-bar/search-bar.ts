import { Component, inject, Input } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-search-bar',
  imports: [ReactiveFormsModule],
  templateUrl: './search-bar.html',
  styleUrl: './search-bar.css',
})
export class SearchBar {
  router = inject(Router);

  queryForm = new FormGroup({
    query: new FormControl(''),
  });

  @Input() styleVar: string = '';

  searchStock(): void {
    const query = this.queryForm.value?.query?.toUpperCase();
    if (!query) return;

    this.router.navigate(['/search'], {
      queryParams: { q: query },
    });
  }
}
