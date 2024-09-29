import { Component, EventEmitter, Input, Output, SimpleChange, type OnInit } from '@angular/core';
import { FormControl } from '@angular/forms';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Pupil } from 'src/app/models/Pupil';
import { PupilService } from '../services/pupil.service';

@Component({
    selector: 'pupils-select-modal',
    templateUrl: './pupils.select.modal.component.html',
    styleUrl: './pupils.select.modal.component.css',
})
export class PupilsSelectModalComponent {

    filter = new FormControl('');
    @Input() pupils: Pupil[] = [];
    @Input() currentPupils: Pupil[] = [];
    @Output() resultPupilIds = new EventEmitter<number[]>;
    fiteredPupils: Pupil[] = [];
    selectedPupils: {[key: number]: boolean} = [];

    constructor(
      private modalService: NgbModal,
      private pupilService: PupilService,
    ) {}

    open(content: any) {
      this.fiteredPupils = this.pupils;
      this.pupils = this.pupils;
      for (let pupil of this.pupils) {
        if (!(pupil.id in this.selectedPupils)) {
          this.selectedPupils[pupil.id] = false;
        }
      }
      this.filter.valueChanges.subscribe(text => {
        let search = text?.toLowerCase() ?? '';
        this.fiteredPupils = this.pupils.filter(pupil => {
          return pupil.username.toLowerCase().includes(search) ||
            pupil.name.toLowerCase().includes(search) ||
            pupil.surname.toLowerCase().includes(search) ||
            pupil.patronymic.toLowerCase().includes(search);
        })
      })
      this.modalService.open(content, {ariaLabelledBy: 'modal-basic-title', size: 'xl'});
    }

    save() {
      let selectedPupilIds: number[] = [];
      for (let id in this.selectedPupils) {
        if (this.selectedPupils[id]) {
          selectedPupilIds.push(Number(id));
        }
      }
      this.resultPupilIds.emit(selectedPupilIds);
      this.modalService.dismissAll();
    }

    ngOnChanges(changes: SimpleChange) {
      if ('currentPupils' in changes) {
        for (let pupilId in this.selectedPupils) {
          this.selectedPupils[pupilId] = false;
        }
        for(let pupil of this.currentPupils) {
          this.selectedPupils[pupil.id] = true;
        }
      }
    }
}
