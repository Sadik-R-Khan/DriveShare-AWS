import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { BookingService } from '../../booking';

@Component({
  selector: 'app-user-dashboard',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './user-dashboard.html',
})
export class UserDashboardComponent implements OnInit {
  loading = true;
  error = '';

  constructor(public bookingService: BookingService) {}

  ngOnInit() {
    const createdBooking = history.state?.booking;
    if (createdBooking?.id) {
      this.bookingService.myBookings.set([createdBooking]);
    }

    this.bookingService.getMine().subscribe({
      next: (b) => {
        if (Array.isArray(b) && b.length > 0) {
          this.bookingService.myBookings.set(b);
        }
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
    this.bookingService.cancel(id).subscribe();
  }
}
