import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

import { RenterService } from './renter';

describe('RenterService', () => {
  let service: RenterService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(RenterService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
