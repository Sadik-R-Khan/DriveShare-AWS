import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CarService } from '../../car';
import { BookingService } from '../../booking';
import { RenterService } from '../../renter';

@Component({
  selector: 'app-renter-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './renter-dashboard.html',
})
export class RenterDashboardComponent implements OnInit {
  cars: any[] = [];
  bookings: any[] = [];
  renter: any = null;
  tab = 'cars';
  showForm = false;
  msg = '';
  isError = false;
  newCar: any = this.emptyCar();

  constructor(
    private carService: CarService,
    private bookingService: BookingService,
    private renterService: RenterService,
  ) {}

  ngOnInit() {
    this.renterService.getMyProfile().subscribe({
      next: (r) => {
        this.renter = r;
        this.carService.getByRenter(r.id).subscribe((c) => (this.cars = c));
        this.bookingService.getByRenter(r.id).subscribe((b) => (this.bookings = b));
      },
      error: () => {
        this.msg = 'Unable to load renter profile.';
        this.isError = true;
      },
    });
  }

  addCar() {
    this.msg = '';
    this.isError = false;

    this.carService.create(this.newCar).subscribe({
      next: (c) => {
        this.cars.push(c);
        this.newCar = this.emptyCar();
        this.showForm = false;
        this.msg = 'Car added successfully.';
      },
      error: (err) => {
        this.msg = err.error?.message || 'Failed to add car';
        this.isError = true;
      },
    });
  }

  deleteCar(id: number) {
    if (!confirm('Delete this car?')) return;
    this.carService.delete(id).subscribe(() => (this.cars = this.cars.filter((c) => c.id !== id)));
  }

  updateStatus(id: number, status: string) {
    this.bookingService
      .updateStatus(id, status)
      .subscribe((b) => (this.bookings = this.bookings.map((bk) => (bk.id === id ? b : bk))));
  }

  toggleForm() {
    this.showForm = !this.showForm;
    this.msg = '';
    this.isError = false;
  }

  setImageFromFile(event: Event) {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) return;

    const reader = new FileReader();
    reader.onload = () => {
      this.newCar.imageData = String(reader.result || '');
    };
    reader.readAsDataURL(file);
  }

  private emptyCar() {
    return {
      make: '',
      model: '',
      year: 2024,
      pricePerDay: 0,
      fuelType: 'PETROL',
      transmission: 'MANUAL',
      seatingCapacity: 5,
      location: '',
      color: '',
      licensePlate: '',
      imageData: '',
      description: '',
    };
  }
}
