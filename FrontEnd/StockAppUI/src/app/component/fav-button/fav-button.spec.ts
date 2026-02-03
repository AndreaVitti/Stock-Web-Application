import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FavButton } from './fav-button';

describe('FavButton', () => {
  let component: FavButton;
  let fixture: ComponentFixture<FavButton>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FavButton]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FavButton);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
