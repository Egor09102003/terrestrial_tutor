import {Component, OnDestroy, OnInit} from '@angular/core';
import {TutorService} from "./services/tutor.service";
import {Router} from "@angular/router";
import {dataService} from "./services/data.service";
import {Homework} from "../../models/Homework";
import {TutorDataService} from "./storage/tutor.data.service";

@Component({
  selector: 'app-tutor',
  templateUrl: './tutor.component.html',
  styleUrls: ['./tutor.component.css']
})
export class TutorComponent implements OnInit {

  constructor(private tutorService: TutorService,
              private router: Router,
              private dataService: dataService,
              private tutorDataService: TutorDataService,) { }

  currentSubjects: any;
  activeTab = 1;
  pageLoaded: boolean =  true;
  homeworks: Homework[] = [];

  ngOnInit(): void {
    let homeworkId = Number(sessionStorage.getItem("homeworkId"));
    let tab = sessionStorage.getItem('tab')
    if (tab == '2') {
      this.activeTab = 2;
      this.getHomeworks();
    }
    if (homeworkId && sessionStorage.getItem('pid') != '1') {
      this.tutorService.deleteHomeworkById(homeworkId).subscribe(() => {
        sessionStorage.clear();
        this.tutorService.getTutorSubjects().subscribe(subjects =>
          this.currentSubjects = subjects);
      });
    } else {
      sessionStorage.clear();
      this.tutorService.getTutorSubjects().subscribe(subjects =>
        this.currentSubjects = subjects);
    }
  }

  addHW(subject: any) {
    this.pageLoaded = false;
    this.tutorService.createHomework(subject).subscribe(homework => {
      if (homework && homework.id) {
        this.pageLoaded = true;
        this.tutorDataService.setHomework(homework);
        sessionStorage.setItem('homeworkId', JSON.stringify(homework.id));
        this.router.navigate(['/tutor/constructor']);
      }
    });
  }

  getHomeworks() {
    this.tutorService.getHomeworks().subscribe(homeworks => this.homeworks = homeworks);
  }

  addPupils(homework: Homework) {
    this.tutorDataService.setHomework(homework);
    sessionStorage.setItem('homeworkId', String(homework.id));
    sessionStorage.setItem('pid', '1');
    this.router.navigate(['tutor/constructor']);
  }

  setTab(tab: number) {
    this.activeTab = tab;
  }
}
