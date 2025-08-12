import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-pagination',
  imports: [CommonModule],
  templateUrl: './pagination.html',
  styleUrl: './pagination.css'
})
export class Pagination {

  @Input() roomPerPage: number = 10; 
  @Input() totalRooms: number = 0;
  @Input() currentPage: number = 1; 
  @Output() paginate: EventEmitter<number> = new EventEmitter<number>();

  get pageNumbers(): number[] {
    const pageCount = Math.ceil(this.totalRooms / this.roomPerPage);
    return Array.from({ length: pageCount }, (_, i) => i + 1);
  }

  onPageChange(pageNumber: number): void {
    this.paginate.emit(pageNumber); 
  }

}
