import {Component, OnInit} from '@angular/core';
import {TutorService} from "./services/tutor.service";
import {ActivatedRoute, Router} from "@angular/router";
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
              private tutorDataService: TutorDataService,
              private route: ActivatedRoute,
              ) { }

  currentSubjects: any;
  activeTab = 1;
  pageLoaded: boolean =  true;
  homeworks: Homework[] = [];
  tutorId: string | null = null;

  ngOnInit(): void {
    this.tutorId = this.route.snapshot.paramMap.get('id');

    let tab = sessionStorage.getItem('tab')
    if (tab == '2') {
      this.activeTab = 2;
      this.getHomeworks();
    }

    this.tutorService.getTutorSubjects().subscribe(subjects =>
      this.currentSubjects = subjects);
  }

  addHW(subject: any) {
    this.pageLoaded = false;
    this.tutorService.createHomework(subject).subscribe(homework => {
      if (homework && homework.id) {
        this.pageLoaded = true;
        let hwId = homework.id;
        this.router.navigate([`/tutor/${this.tutorId}/constructor/${hwId}`]);
      }
    });
  }

  getHomeworks() {
    this.tutorService.getHomeworks().subscribe(homeworks => {
      this.homeworks = homeworks.filter((homework: Homework)=> homework.name)
    });
  }

  addPupils(homework: Homework) {
    this.tutorDataService.setHomework(homework);
    sessionStorage.setItem('homeworkId', String(homework.id));
    sessionStorage.setItem('pid', '1');
    this.router.navigate([`tutor/${this.tutorId}/constructor/${homework.id}`]);
  }

  setTab(tab: number) {
    this.activeTab = tab;
  }

  filterHomeworks(subject: string) {
    return this.homeworks.filter(homework => homework.subject === subject);
  }

  protected readonly Homework = Homework;
}
