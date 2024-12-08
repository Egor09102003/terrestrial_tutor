import {Component, OnInit} from '@angular/core';
import {TutorService} from "./services/tutor.service";
import {ActivatedRoute, Router} from "@angular/router";
import {Homework} from "../../models/Homework";
import { homeworkProps } from 'src/app/models/enums/HomeworkProps';

@Component({
  selector: 'app-tutor',
  templateUrl: './tutor.component.html',
  styleUrls: ['./tutor.component.css']
})
export class TutorComponent implements OnInit {

  constructor(private tutorService: TutorService,
              private router: Router,
              private route: ActivatedRoute,
              ) { }

  currentSubjects: any;
  activeTab = 1;
  pageLoaded: boolean =  true;
  homeworks: Homework[] = [];
  tutorId: number;
  checkingHomeworks: Homework[];
  homeworkProps = homeworkProps;

  ngOnInit(): void {
    this.tutorId = Number(this.route.snapshot.paramMap.get('id'));

    this.activeTab = Number(this.route.snapshot.queryParamMap.get('tab')) === 0 ? 1 : Number(this.route.snapshot.queryParamMap.get('tab'));
    if (this.activeTab !== 1) {
      this.getCheckingHomeworks();
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

  getCheckingHomeworks() {
    this.tutorService.getTutorHomeworks().subscribe(homeworks => this.homeworks = homeworks.filter((homework: Homework)=> homework.name));
  }

  navigate(tab: number) {
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: {tab: tab},
      queryParamsHandling: 'merge'
    })
  }

  public readonly Object = Object;
  public readonly Homework = Homework;
}
