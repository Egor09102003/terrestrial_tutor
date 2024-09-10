import { Component, Input, TemplateRef, type OnInit } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Pupil } from 'src/app/models/Pupil';
import { PupilService } from '../../pupil/services/pupil.service';

@Component({
    selector: 'pupils-modal',
    templateUrl: './pupils.modal.component.html',
    styleUrl: './pupils.modal.component.css',
})
export class PupilsModalComponent implements OnInit {

    constructor(
        private modalService: NgbModal,
        private pupilService: PupilService
    ) {}

    @Input() pupilIds: number[];
    @Input() tutorId: number;
    @Input() homeworkId: number;
    pupils: Pupil[];

    ngOnInit(): void {
        
    }

    open(content: TemplateRef<any>) {
        this.pupilService.getPupilByIds(this.pupilIds).subscribe(pupils => {
            this.pupils = pupils;
        });
		this.modalService.open(content, { ariaLabelledBy: 'modal-basic-title' }).result.then();
	}

}
