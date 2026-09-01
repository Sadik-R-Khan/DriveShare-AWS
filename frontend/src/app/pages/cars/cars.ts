import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { CarService } from '../../car';

@Component({
  selector: 'app-cars',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './cars.html',
})
export class CarsComponent implements OnInit {
  cars: any[] = [];
  query = '';

  constructor(
    private carService: CarService,
    private router: Router,
  ) {}

  ngOnInit() {
    this.load();
  }

  load() {
    this.carService.getAll().subscribe((c) => (this.cars = c));
  }

  search() {
    if (!this.query.trim()) {
      this.load();
      return;
    }
    this.carService.search(this.query).subscribe((c) => (this.cars = c));
  }

  clearSearch() {
    this.query = '';
    this.load();
  }

  imageSrc(car: any) {
    return car.imageData || car.imageUrl || '';
  }

  go(car: any) {
    this.router.navigate(['/book', car.id], { state: { car } });
  }
}
