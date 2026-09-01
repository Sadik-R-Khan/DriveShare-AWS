import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { BookingService } from '../../booking';

@Component({
  selector: 'app-user-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './user-dashboard.html',
})
export class UserDashboardComponent implements OnInit {
  bookings: any[] = [];
  loading = true;
  error = '';

  constructor(private bookingService: BookingService) {}

  ngOnInit() {
    const createdBooking = history.state?.booking;
    if (createdBooking?.id) {
      this.bookings = [createdBooking];
    }

    this.bookingService.getMine().subscribe({
      next: (b) => {
        this.bookings = Array.isArray(b) && b.length > 0 ? b : this.bookings;
        this.loading = false;
      },
      error: (err) => {
        this.error =
          err.name === 'TimeoutError'
            ? 'Bookings are taking too long to load. Please refresh or try again.'
            : err.error?.message || err.message || 'Could not load your bookings.';
        this.loading = false;
      },
    });
  }

  cancel(id: number) {
    if (!confirm('Cancel this booking?')) return;
    this.bookingService
      .cancel(id)
      .subscribe(
        () =>
          (this.bookings = this.bookings.map((b) =>
            b.id === id ? { ...b, status: 'CANCELLED' } : b,
          )),
      );
  }
}
