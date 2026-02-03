import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InvestModal } from './invest-modal';

describe('InvestModal', () => {
  let component: InvestModal;
  let fixture: ComponentFixture<InvestModal>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InvestModal]
    })
    .compileComponents();

    fixture = TestBed.createComponent(InvestModal);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
