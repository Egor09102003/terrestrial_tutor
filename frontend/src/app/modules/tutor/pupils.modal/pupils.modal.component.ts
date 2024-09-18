import { Component, Input, TemplateRef, type OnInit } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Pupil } from 'src/app/models/Pupil';
import { PupilService } from '../../pupil/services/pupil.service';
import { HomeworkAnswers } from 'src/app/models/HomeworkAnswers';
import { pupilStatisticFields } from 'src/app/models/PupilsStatisticFields';

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
    pupilFields = pupilStatisticFields;

    ngOnInit(): void {
        
    }

    open(content: TemplateRef<any>) {
        this.pupilService.getPupilByIds(this.pupilIds, this.homeworkId).subscribe(pupils => {
            this.pupils = pupils;
        });
		this.modalService.open(content, { ariaLabelledBy: 'modal-basic-title', size: 'lg'}).result;
	}

    getAttemptPoints(attempt: HomeworkAnswers): number {
        let points = 0;
        for(let taskId in attempt.answersStatuses) {
            points += attempt.answersStatuses[taskId].points;
        }
        return points;
    }

}
