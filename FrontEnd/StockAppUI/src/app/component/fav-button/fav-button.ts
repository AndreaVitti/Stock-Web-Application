import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-fav-button',
  imports: [],
  templateUrl: './fav-button.html',
  styleUrl: './fav-button.css',
})
export class FavButton {
  router = inject(Router);
  
  openFav() {
    this.router.navigate(['/favourites']);
  }
}

