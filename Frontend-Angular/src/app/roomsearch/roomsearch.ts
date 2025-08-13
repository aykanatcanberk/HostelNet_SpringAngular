import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import {FormsModule} from '@angular/forms'
import { Api } from '../service/api';

@Component({
  selector: 'app-roomsearch',
  imports: [CommonModule, FormsModule],
  templateUrl: './roomsearch.html',
  styleUrl: './roomsearch.css'
})
export class Roomsearch implements OnInit{
  
  @Output() searchResults = new EventEmitter<any[]>(); 

  startDate: string | null = null; 
  endDate: string | null = null; 
  roomType: string = '';
  roomTypes: string[] = [];
  error: any = null;

  minDate: string = new Date().toISOString().split('T')[0];

  constructor(private apiService: Api) {}

  ngOnInit(): void {
    this.fetchRoomTypes();
  }

  fetchRoomTypes() {
    this.apiService.getRoomTypes().subscribe({
      next: (types: any) => {
        this.roomTypes = types;
      },
      error: (err:any) => {
        this.showError(
          err?.error?.message || 'Error Fetching Room Types: ' + err
        );
        console.error(err);
      },
    });
  }

  showError(msg: string): void {
    this.error = msg;
    setTimeout(() => {
      this.error = null;
    }, 5000);
  }

  handleSearch() {
    if (!this.startDate || !this.endDate || !this.roomType) {
      this.showError('Please select all fields');
      return;
    }

    const formattedStartDate = new Date(this.startDate);
    const formattedEndDate = new Date(this.endDate);

    if (
      isNaN(formattedStartDate.getTime()) ||
      isNaN(formattedEndDate.getTime())
    ) {
      this.showError('Invalid date format');
      return;
    }

    const startDateStr = formattedStartDate.toLocaleDateString('en-CA'); 
    const endDateStr = formattedEndDate.toLocaleDateString('en-CA'); 

    console.log('formattedStartDate: ' + startDateStr);
    console.log('formattedEndDate: ' + endDateStr);
    console.log('roomType: ' + this.roomType);

    this.apiService
      .getAvailableRooms(startDateStr, endDateStr, this.roomType)
      .subscribe({
        next: (resp: any) => {
          if (resp.rooms.length === 0) {
            this.showError(
              'Room type not currently available for the selected date'
            );
            return;
          }
          this.searchResults.emit(resp.rooms); 
          this.error = ''; 
        },
        error: (error:any) => {
          this.showError(error?.error?.message || error.message);
        },
      });
  }

}
