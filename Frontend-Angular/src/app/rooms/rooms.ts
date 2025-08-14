import { Component } from '@angular/core';
import { Pagination } from '../pagination/pagination';
import { Roomresult } from '../roomresult/roomresult';
import { Roomsearch } from '../roomsearch/roomsearch';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Api } from '../service/api';

@Component({
  selector: 'app-rooms',
  imports: [
    Pagination,
    Roomresult,
    Roomsearch,
    CommonModule, 
    FormsModule],
  templateUrl: './rooms.html',
  styleUrl: './rooms.css'
})
export class Rooms {

  rooms: any[] = [];
  filteredRooms: any[] = [];
  roomTypes: string[] = [];
  selectedRoomType: string = '';
  currentPage: number = 1;
  roomsPerPage: number = 4;
  error: any = null;

  constructor(private apiService: Api) {}
  
  ngOnInit():void{

    this.fetchRooms();
    this.fetchRoomTypes();

  }

  showError(msg: string): void {
    this.error = msg;
    setTimeout(() => {
      this.error = null;
    }, 5000);
  }

  fetchRooms() {
    this.apiService.getAllRooms().subscribe({
      next: (response: any) => {
        this.rooms = response.rooms;
        this.filteredRooms = response.rooms;
      },
      error: (err) => {
        this.showError(err?.error?.message || 'Error fetching rooms:' + err);
      },
    });
  }

  fetchRoomTypes() {
    this.apiService.getRoomTypes().subscribe({
      next: (types: string[]) => {
        this.roomTypes = types;
      },
      error: (err) => {
        this.showError(
          err?.error?.message || 'Error fetching room Types:' + err
        );
      },
    });
  }


    handleSearchResult(results: any[]) {
      this.rooms = results;
      this.filteredRooms = results;
    }
  
    handleRoomTypeChange(event: any) {
      const selectedType = event.target.value;
      this.selectedRoomType = selectedType;
      this.filterRooms(selectedType);
    }

  filterRooms(type: string) {
    if (type === '') {
      this.filteredRooms = this.rooms;
    } else {
      this.filteredRooms = this.rooms.filter((room) => room.type === type);
    }
    this.currentPage = 1;
  }


  get indexOfLastRoom() {
    return this.currentPage * this.roomsPerPage;
  }

  get indexOfFirstRoom() {
    return this.indexOfLastRoom - this.roomsPerPage;
  }

  get currentRooms() {
    return this.filteredRooms.slice(
      this.indexOfFirstRoom,
      this.indexOfLastRoom
    );
  }

  paginate(pageNumber: number) {
    this.currentPage = pageNumber;
  }

}
