import { TestBed } from '@angular/core/testing';

import { InvestmentApi } from './investment-api';

describe('InvestmentApi', () => {
  let service: InvestmentApi;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(InvestmentApi);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
