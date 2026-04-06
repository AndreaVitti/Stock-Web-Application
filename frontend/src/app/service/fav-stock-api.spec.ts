import { TestBed } from '@angular/core/testing';

import { FavStockApi } from './fav-stock-api';

describe('FavStockApi', () => {
  let service: FavStockApi;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FavStockApi);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
