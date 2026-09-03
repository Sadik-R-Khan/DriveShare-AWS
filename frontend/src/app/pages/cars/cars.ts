import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CarService } from '../../car';

@Component({
  selector: 'app-cars',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './cars.html',
})
export class CarsComponent implements OnInit {
  query = '';

  constructor(
    public carService: CarService,
    private router: Router,
  ) {}

  ngOnInit() {
    this.carService.loadAll();
  }

  search() {
    if (!this.query.trim()) {
      this.carService.loadAll();
      return;
    }
    this.carService.search(this.query).subscribe();
  }

  clearSearch() {
    this.query = '';
    this.carService.loadAll();
  }

  imageSrc(car: any) {
    return car.imageData || car.imageUrl || '';
  }

  go(car: any) {
    this.router.navigate(['/book', car.id], { state: { car } });
  }
}
