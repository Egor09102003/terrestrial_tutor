import { CommonModule } from '@angular/common';
import { Component, Input, TemplateRef, type OnInit } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { HomeworkService } from '../../homework/services/homework.service.';
import { TutorList } from 'src/app/models/TutorList';
import { TutorListSelect } from 'src/app/models/TutorListSelect';
import { tutorProps } from 'src/app/models/TutorProps';
import { TutorService } from '../../tutor/services/tutor.service';
import { AdminService } from '../services/admin.service';

@Component({
    selector: 'homework-tutors-set-modal',
    templateUrl: './homework.tutors.set.modal.component.html',
    styleUrl: './homework.tutors.set.modal.component.css',
})
export class HomeworkTutorsSetModalComponent implements OnInit {

    @Input() homeworkId: number;
    @Input() subject: string;
    tutorsSelect: {[key: number]: TutorListSelect} = {};
    modalLoaded = false; 
    Object = Object;
    TutorProps = tutorProps;

    constructor(
        private modalService: NgbModal,
        private homeworkService: HomeworkService,
        private adminService: AdminService,
    ) {
    }

    ngOnInit(): void { 
        
    }

    open(content: TemplateRef<any>) {
        this.adminService.findTutorsBySubject(this.subject).subscribe(tutors => {
            for (let tutor of tutors) {
                this.tutorsSelect[tutor.id] = new TutorListSelect(tutor, false);
            }
            this.homeworkService.getHomeworkTutors(this.homeworkId).subscribe(tutors => {
                tutors = <TutorList[]> tutors;
                for(let tutor of tutors) {
                    if (tutor.id in this.tutorsSelect) {
                        this.tutorsSelect[tutor.id].isSelected = true;
                    }
                }
                this.modalLoaded = true;
            });
            this.modalService.open(content, { ariaLabelledBy: 'modal-basic-title', size: 'lg'}).result;
        })
    }

    save() {
        let selectedTutors = [];
        for (let tutor of this.Object.keys(this.tutorsSelect)) {
            if (this.tutorsSelect[Number(tutor)].isSelected) {
                selectedTutors.push(Number(tutor));
            }
        }
        this.homeworkService.addHomeworkTutors(this.homeworkId, selectedTutors).subscribe();
    }
}
