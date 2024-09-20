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
  pupilsSubject = "Выбирете предмет";
  tutorsSubject = "Выбирете предмет";
  subjects: Subject[] | undefined;
  tutorsBySubjectsPupil: TutorList[] = [];
  tutorsBySubjectsTutor: TutorList[] = [];
  // @ts-ignore
  selectedTutor: TutorList;
  pupils: Pupil[] = [];
  filteredTutorsPupils: TutorList[] = [];
  filteredTutors: TutorList[] = [];
  isChecksPageLoaded: boolean = false;
  isNewDataLoaded: boolean = true;
  isNewPupilsLoaded: boolean = true;
  newData: (PupilSelect | TutorListSelect)[] = [];
  active = "requests";
  filter = new UntypedFormControl('');
  filteredData: (PupilSelect | TutorListSelect)[] = [];
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

    this.pupilService.getAll().subscribe(pupils => {
      this.pupils.push(pupils[0]);
      this.pupils.push(pupils[1]);
    })
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

  findTutorsForPupilsAdd() {
    this.adminService.findTutorsBySubject(this.pupilsSubject).subscribe(tutors => {
      this.tutorsBySubjectsPupil = tutors;
      this.filteredTutorsPupils = this.tutorsBySubjectsPupil;
    })
  }

  findTutorsForTutorsAdd() {
    this.adminService.findTutorsBySubject(this.tutorsSubject).subscribe(tutors => {
      this.tutorsBySubjectsTutor = tutors;
      this.filteredTutors = this.tutorsBySubjectsTutor;
    })
  }

  getTutorPupilsBySubject() {
    this.isNewDataLoaded = false;
    this.adminService.getTutorPupilsBySubject(this.pupilsSubject, this.selectedTutor.id).subscribe(data => {
      this.pupils = data;
      this.isNewDataLoaded = true;
    })
  }

  search(text: any) {
    text = text.toLowerCase();
    this.filteredData = this.newData.filter(data => {
      if (data instanceof PupilSelect) {
        return data.pupil.username.toLowerCase().includes(text) ||
          data.pupil.name.toLowerCase().includes(text) ||
          data.pupil.surname.toLowerCase().includes(text) ||
          data.pupil.patronymic.toLowerCase().includes(text);
      } else {
        return data.tutor.username.toLowerCase().includes(text) ||
          data.tutor.name.toLowerCase().includes(text) ||
          data.tutor.surname.toLowerCase().includes(text) ||
          data.tutor.patronymic.toLowerCase().includes(text);
      }
    });
  }

  open(content: any, typePupils: boolean = true) {
    this.newData = [];
    if (typePupils) {
      this.adminService.findPupilsWithoutSubject(this.pupilsSubject).subscribe(pupils => {
        this.newData = pupils.map((pupil: any) => {
          return new PupilSelect(pupil, false);
        });
        this.filteredData = this.newData;
        this.isNewDataLoaded = true;
      });
    } else {
      this.adminService.findTutorsWithoutSubject(this.tutorsSubject).subscribe(tutors => {
        this.newData = tutors.map((tutor: any) => {
          return new TutorListSelect(tutor, false);
        });;
        this.filteredData = this.newData;
        this.isNewDataLoaded = true;
      })
    }
    this.filter.valueChanges.subscribe((text) => {
      this.search(text);
    });
    this.modalService.open(content, {ariaLabelledBy: 'modal-basic-title', size: 'lg'}).result.then(selectedData => {
      let newDataIds = selectedData.map((data: Pupil | TutorList) => {
        return data.id;
      })
      if (typePupils) {
        this.pupilService.addSubjects(this.pupilsSubject, newDataIds).subscribe();
        this.adminService.addPupilsForTutor(newDataIds, this.selectedTutor.id, this.pupilsSubject).subscribe(pupils => {
          this.pupils = pupils;
        });
      } else {
        this.adminService.addTutorsSubject(newDataIds, this.tutorsSubject).subscribe(tutors => this.tutorsBySubjectsTutor = tutors);
      }
    }).finally(() => {});
    this.isNewDataLoaded = false;
  }

  close(modal: any) {
    modal.close(this.newData.filter(data => data.isSelected).map(data => {
      return data instanceof PupilSelect ? data.pupil : data.tutor;
    }));
  }

  navChange() {
    if (this.active == "setTutors") {
      this.getAllSubjects();
    }
  }

  checkType(data: PupilSelect | TutorListSelect) {
    return data instanceof PupilSelect ? data.pupil : data.tutor;
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
}
