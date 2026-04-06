import { Routes } from '@angular/router';
import { Home } from './component/home/home';
import { SearchResults } from './component/search-results/search-results';
import { Favourites } from './component/favourites/favourites';
import { Investments } from './component/investments/investments';
import { ErrorPage } from './component/error-page/error-page';

export const routes: Routes = [
    {
        path: '', component: Home
    },
    {
        path: 'search', component: SearchResults
    },
    {
        path: 'favourites', component: Favourites
    },
    {
        path: 'investments', component: Investments
    },
    {
        path: 'errorPage', component: ErrorPage
    }
];
