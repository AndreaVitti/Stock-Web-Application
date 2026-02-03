import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { FavButton } from '../fav-button/fav-button';
import { SearchBar } from '../search-bar/search-bar';
import { Investments } from '../investments/investments';
import { InvButtton } from '../inv-buttton/inv-buttton';


@Component({
  selector: 'app-home',
  imports: [FormsModule, FavButton, SearchBar, InvButtton],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home {
}
