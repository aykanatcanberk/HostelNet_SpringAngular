import { Component, ChangeDetectorRef, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Api } from '../service/api';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-roomdetails',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './roomdetails.html',
  styleUrls: ['./roomdetails.css']
})
export class Roomdetails implements OnInit {

  room: any = null;
  roomId: string = '';
  checkInDate: Date | null = null;
  checkOutDate: Date | null = null;
  totalPrice = 0;
  totalDaysToStay = 0;
  showDatePicker = false;
  showBookingPreview = false;
  message: string | null = null;
  error: string | null = null;
  readonly minDate: string = new Date().toISOString().split('T')[0];

  constructor(
    private readonly apiService: Api,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.roomId = this.route.snapshot.paramMap.get('id') ?? '';
    if (this.roomId) {
      this.fetchRoomDetails(this.roomId);
    }
  }

  fetchRoomDetails(roomId: string): void {
    this.apiService.getRoomById(roomId).subscribe({
      next: (res: any) => {
        this.room = res.room;
        this.cdr.detectChanges(); // 👈 Angular'a "güncelle" dedik
      },
      error: (err) => {
        this.showError(err?.error?.message || 'Unable to fetch room details');
        this.cdr.detectChanges();
      }
    });
  }

  showError(err: string): void {
    console.error(err);
    this.error = err;
    setTimeout(() => {
      this.error = null;
      this.cdr.detectChanges();
    }, 5000);
  }

  calculateTotalPrice(): number {
    if (!this.checkInDate || !this.checkOutDate) return 0;

    const checkIn = new Date(this.checkInDate);
    const checkOut = new Date(this.checkOutDate);
    if (isNaN(checkIn.getTime()) || isNaN(checkOut.getTime())) {
      this.showError('Invalid Date selected');
      return 0;
    }

    const oneDay = 24 * 60 * 60 * 1000;
    const totalDays = Math.round((checkOut.getTime() - checkIn.getTime()) / oneDay);
    this.totalDaysToStay = totalDays;

    return this.room?.pricePerNight * totalDays || 0;
  }

  handleConfirmation(): void {
    if (!this.checkInDate || !this.checkOutDate) {
      this.showError('Please select both check-in and check-out dates');
      return;
    }

    this.totalPrice = this.calculateTotalPrice();
    this.showBookingPreview = true;
    this.cdr.detectChanges();
  }

  acceptBooking(): void {
    if (!this.room) return;

    const booking = {
      checkInDate: this.checkInDate ? new Date(this.checkInDate).toLocaleDateString('en-CA') : '',
      checkOutDate: this.checkOutDate ? new Date(this.checkOutDate).toLocaleDateString('en-CA') : '',
      roomId: this.roomId
    };

    this.apiService.bookRoom(booking).subscribe({
      next: (res: any) => {
        if (res.status === 200) {
          this.message = 'Your Booking is Successful. An Email of your booking details and the payment link has been sent.';
          this.cdr.detectChanges();
          setTimeout(() => {
            this.message = null;
            this.router.navigate(['/rooms']);
          }, 8000);
        }
      },
      error: (err) => {
        this.showError(err?.error?.message || 'Unable to make a booking');
        this.cdr.detectChanges();
      }
    });
  }

  cancelBookingPreview(): void {
    this.showBookingPreview = false;
    this.cdr.detectChanges();
  }

  get isLoading(): boolean {
    return !this.room;
  }
}
