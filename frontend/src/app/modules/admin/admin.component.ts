import {Component, OnInit, PipeTransform} from '@angular/core';
import {Check} from "../../models/Check";
import {AdminService} from "./services/admin.service";
import {TokenStorageService} from "../../security/token-storage.service";
import {Subject} from "../../models/Subject";
import {SubjectsService} from "../subjects/services/subjects.service";
import {PupilService} from "../pupil/services/pupil.service";
import {TutorList} from "../../models/TutorList";
import {Pupil} from "../../models/Pupil";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {PupilSelect} from "../../models/PupilSelect";
import {UntypedFormControl} from "@angular/forms";
import { TutorService } from '../tutor/services/tutor.service';
import { TutorListSelect } from 'src/app/models/TutorListSelect';
import { HomeworkService } from '../homework/services/homework.service.';
import { Homework } from 'src/app/models/Homework';
import { homeworkProps } from 'src/app/models/HomeworkProps';

@Component({
  selector: 'app-admin',
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.css'],
})
export class AdminComponent implements OnInit {

  checks: Check[] = []
  currentSubject = "Выбирете предмет";
  currentTutor: TutorList;
  subjects: Subject[] | undefined;
  pupils: Pupil[] = [];
  isChecksPageLoaded: boolean = false;
  isNewDataLoaded: boolean = true;
  active = "requests";
  homeworks: Homework[] = [];
  homeworkProps = homeworkProps;
  tutors: TutorList[] = [];
  Object = Object;

  constructor(private adminService: AdminService,
              private tokenService: TokenStorageService,
              private subjectsService: SubjectsService,
              private pupilService: PupilService,
              private modalService: NgbModal,
              private tutorService: TutorService,
              private homeworkService: HomeworkService) {
  }

  ngOnInit(): void {
    this.adminService.getAllChecks()
      .subscribe(checks => {
        this.checks = checks;
        this.isChecksPageLoaded = true;
      });
  }

  deleteCheck(id: number) {
    this.adminService.deleteCheck(id).subscribe(check => {
      this.checks.splice(check);
      window.location.reload();
    });
  }

  getAllSubjects(): void {
    this.subjectsService.getAllSubjects().subscribe(data => {
      this.subjects = data;
    })
  }

  getTutorPupilsBySubject() {
    this.isNewDataLoaded = false;
    this.adminService.getTutorPupilsBySubject(this.currentSubject, this.currentTutor.id).subscribe(data => {
      this.pupils = data;
      this.isNewDataLoaded = true;
    })
  }

  navChange() {
    if (this.active == "setTutors") {
      this.getAllSubjects();
    }
  }

  getHomeworks() {
    this.homeworkService.getAllHomeworks().subscribe(homeworks => {
      this.homeworks = <Homework[]>homeworks
    });
    this.tutorService.getAllTutors().subscribe(tutors => this.tutors = tutors);
  }

  test(test: number[]) {
    console.log(test);
  }

  setSubject(subject: Subject) {
    this.currentSubject=subject.subjectName;
    this.tutorService.getAllTutors().subscribe(tutors => this.tutors = tutors);
  }
}
