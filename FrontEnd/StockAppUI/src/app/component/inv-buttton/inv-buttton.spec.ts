import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InvButtton } from './inv-buttton';

describe('InvButtton', () => {
  let component: InvButtton;
  let fixture: ComponentFixture<InvButtton>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InvButtton]
    })
    .compileComponents();

    fixture = TestBed.createComponent(InvButtton);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
