import { Component, EventEmitter, Input, Output, SimpleChange } from '@angular/core';

@Component({
  selector: 'search-select',
  templateUrl: './search-select.component.html',
  styleUrl: './search-select.component.css'
})
export class SearchSelectComponent {

  @Input() values: any[] = [];
  @Input() key: string;
  @Input() printKeys: string[] = [];
  currentItem: any = '';
  @Input() currentValue: any = '';
  @Output() changed = new EventEmitter<any>
  filteredValues: any[] = [];

  ngOnInit() {
    if (this.key && this.currentValue !== '') {
      this.currentItem = this.values.find(item => item[this.key] === this.currentValue);
    } else {
      this.currentItem = this.currentValue;
    }
  }

  constructString(value: any): string {
    let stringValue: string = '';
    if (this.key && this.printKeys.length > 0) {
      let current = this.values.find(item => item[this.key] === value[this.key]);
      if (current) {
        for (let objectKey of this.printKeys) {
          stringValue += current[objectKey] + ' ';
        }
      }
    } else {
      stringValue = value;
    }
    return stringValue.trim();
  }

  search(needle: string) {
    this.filteredValues.filter(
      value => this.constructString(value).includes(needle.trim())
    )
  }

  ngOnChanges(changes: SimpleChange) {
    if (this.values.length > 0) {
      this.filteredValues = this.values;
      if (this.key && this.currentValue !== '') {
        this.currentItem = this.values.find(item => item[this.key] === this.currentValue);
      } else {
        this.currentItem = this.currentValue;
      }
    }
  }

}
