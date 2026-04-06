import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { FavButton } from '../fav-button/fav-button';
import { SearchBar } from '../search-bar/search-bar';
import { InvButtton } from '../inv-buttton/inv-buttton';


@Component({
  selector: 'app-home',
  imports: [FormsModule, FavButton, SearchBar, InvButtton],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home {
}
