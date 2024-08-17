import {Component, Input} from '@angular/core';
import {NgForOf, NgIf} from "@angular/common";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";

@Component({
  selector: 'task-table',
  templateUrl: './task-table.component.html',
  standalone: true,
  imports: [
    NgForOf,
    NgIf,
    FormsModule,
    ReactiveFormsModule
  ],
  styleUrl: './task-table.component.css'
})
export class TaskTableComponent {
  @Input() table: string | undefined;

  decodeTable() {
    if (this.table === '' || !this.table) {
      return null;
    }
    let parsedTable: [[string]] = JSON.parse(this.table);
    for (let i = 0; i < parsedTable.length; i++) {
      for (let j = 0; j < parsedTable[i].length; j++) {
        if (parsedTable[i][j] != '') {
          return parsedTable;
        }
      }
    }
    return null;
  }
}
