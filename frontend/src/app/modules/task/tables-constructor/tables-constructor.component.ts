import {Component, ElementRef, EventEmitter, Input, Output, ViewChild} from '@angular/core';
import {
  FormControl,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  UntypedFormArray,
} from "@angular/forms";
import {NgForOf, NgIf} from "@angular/common";

@Component({
  selector: 'tables-constructor',
  standalone: true,
  imports: [
    FormsModule,
    NgForOf,
    ReactiveFormsModule,
    NgIf
  ],
  templateUrl: './tables-constructor.component.html',
  styleUrl: './tables-constructor.component.css'
})
export class TablesConstructorComponent {
  @Input() width: number;
  @Input() height: number;
  @Input() initTable: string | undefined;
  @Output() resultTable = new EventEmitter<string>;
  @ViewChild('rows') rows: ElementRef;
  @ViewChild('cols') cols: ElementRef;
  tableForm: FormGroup;

  constructor(
  ) {
  }

  ngOnInit() {
    this.tableForm = new FormGroup<any>({
      table: new FormControl([])
    });
    let table: FormControl[][] = [];
    try {
      let currentTable = JSON.parse(<string>this.initTable);
      for (let i = 0; i < currentTable.length; i++) {
        let row = [];
        for (let j = 0; j < currentTable[0].length; j++) {
          row.push(new FormControl(currentTable[i][j]));
        }
        table.push(row);
      }
    } catch (e) {
      table = [[new FormControl(''), new FormControl(''), new FormControl('')]];
    }
    this.tableForm.controls['table'].setValue(table);
  }

  renderTable() {
    let newTable: any[][] = [];
    let rows = this.width ? this.height : this.rows.nativeElement.value;
    let cols = this.height ? this.width : this.cols.nativeElement.value;
    for (let i = 0; i < rows; i++) {
      newTable.push([]);
      for (let j = 0; j < cols; j++) {
        newTable[i].push(new FormControl(''));
      }
    }
    this.tableForm.controls['table'].setValue(newTable);
  }

  get table() {
    return this.tableForm.get('table') as UntypedFormArray;
  }

  returnTable() {
    this.resultTable.emit(this.tableToJson());
  }

  tableToJson() {
    let tableArray: any[][] = [];
    let isEmpty = true;
    let table = this.tableForm.controls['table'].value;
    for (let i = 0; i < table.length; i++) {
      tableArray.push([])
      for (let col of table[i]) {
        tableArray[i].push(col.value);
        if (col.value !== '') {
          isEmpty = false;
        }
      }
    }
    return !isEmpty ? JSON.stringify(tableArray) : '';
  }
}
