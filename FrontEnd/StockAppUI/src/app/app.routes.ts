import { Routes } from '@angular/router';
import { Favourites } from './component/favourites/favourites';
import { Home } from './component/home/home';
import { SearchResults } from './component/search-results/search-results';
import { ErrorPage } from './component/error-page/error-page';
import { Investments } from './component/investments/investments';

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
