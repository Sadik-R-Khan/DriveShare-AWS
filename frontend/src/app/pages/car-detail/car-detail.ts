import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { timeout } from 'rxjs';
import { CarService } from '../../car';
import { BookingService } from '../../booking';
import { AuthService } from '../../auth';

@Component({
  selector: 'app-car-detail',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterLink],
  templateUrl: './car-detail.html',
})
export class CarDetailComponent implements OnInit {
  car: any;
  carBookings: any[] = [];
  today = this.formatDate(new Date());
  startDate = this.today;
  endDate = this.formatDate(new Date(Date.now() + 86400000));
  msg = '';
  isError = false;
  loading = true;
  booking = false;
  loadError = '';

  constructor(
    private route: ActivatedRoute,
    private carService: CarService,
    private bookingService: BookingService,
    public auth: AuthService,
    private router: Router,
  ) {}

  ngOnInit() {
    const selectedCar = history.state?.car;
    if (selectedCar?.id) {
      this.car = selectedCar;
      this.loading = false;
      this.loadCarBookings();
    }

    const idParam = this.route.snapshot.paramMap.get('id');
    const id = Number(idParam);
    if (!idParam || !Number.isFinite(id)) {
      this.loading = false;
      this.loadError = 'This car listing could not be found.';
      return;
    }

    this.carService
      .getById(id)
      .pipe(timeout(8000))
      .subscribe({
        next: (c) => {
          this.car = c;
          this.loading = false;
          this.loadCarBookings();
        },
        error: () => {
          this.loading = false;
          if (!this.car) {
            this.loadError = 'This car listing could not be loaded. Please go back and try again.';
          }
        },
      });
  }

  imageSrc() {
    return this.car?.imageData || this.car?.imageUrl || '';
  }

  private formatDate(date: Date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  get days() {
    if (!this.startDate || !this.endDate) return 0;
    const d = (new Date(this.endDate).getTime() - new Date(this.startDate).getTime()) / 86400000;
    return d > 0 ? d : 0;
  }

  get conflictingBooking() {
    if (!this.car || !this.startDate || !this.endDate) return null;
    const selectedStart = new Date(this.startDate).getTime();
    const selectedEnd = new Date(this.endDate).getTime();

    return (
      this.carBookings.find((booking) => {
        if (booking.carId !== this.car.id || booking.status === 'CANCELLED') return false;
        const bookingStart = new Date(booking.startDate).getTime();
        const bookingEnd = new Date(booking.endDate).getTime();
        return bookingStart < selectedEnd && bookingEnd > selectedStart;
      }) || null
    );
  }

  private loadCarBookings() {
    if (!this.auth.isLoggedIn || this.auth.isRenter || !this.car?.renterId) return;

    this.bookingService.getByRenter(this.car.renterId).subscribe({
      next: (bookings) =>
        (this.carBookings = bookings.filter((booking) => booking.carId === this.car.id)),
      error: () => (this.carBookings = []),
    });
  }

  book() {
    this.msg = '';
    this.isError = false;
    this.booking = false;

    if (!this.auth.isLoggedIn) {
      this.router.navigate(['/login']);
      return;
    }
    if (this.auth.isRenter) {
      this.msg = 'Renters cannot book cars.';
      this.isError = true;
      return;
    }
    if (this.days <= 0) {
      this.msg = 'Choose valid pickup and return dates.';
      this.isError = true;
      return;
    }
    if (this.conflictingBooking) {
      this.msg = 'This car is already booked for the selected dates.';
      this.isError = true;
      return;
    }

    this.booking = true;
    this.bookingService
      .create({
        carId: this.car.id,
        startDate: this.startDate,
        endDate: this.endDate,
        pickupLocation: this.car.location || '',
      })
      .subscribe({
        next: (createdBooking) => {
          this.booking = false;
          this.msg = 'Booking confirmed.';
          this.isError = false;
          this.router.navigate(['/bookings'], { state: { booking: createdBooking } });
        },
        error: (err) => {
          this.booking = false;
          this.msg = err.error?.message || err.message || 'Booking failed.';
          this.isError = true;
        },
      });
  }
}
