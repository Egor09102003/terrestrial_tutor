import { Component } from '@angular/core';
import { PupilDataService } from '../services/pupil.data.service';
import { Pupil } from 'src/app/models/Pupil';
import { PupilService } from '../services/pupil.service';
import { Homework } from 'src/app/models/Homework';
import { ActivatedRoute, Router } from '@angular/router';
import { TutorService } from '../../tutor/services/tutor.service';
import { HomeworkService } from '../../homework/services/homework.service.';

@Component({
  selector: 'app-homeworks.list',
  templateUrl: './homeworks.list.component.html',
  styleUrls: ['./homeworks.list.component.css']
})
export class HomeworksListComponent {

  homeworks: Homework[] = [];
  subject: string = '';
  collapseHomeworks: boolean = true;
  collapseHomeworksStatistic: boolean = true;
  completedHomeworks: {[key: number]: number} = {};
  pupilId: number;

  constructor(private pupilDataService: PupilDataService,
    private pupilService: PupilService,
    private router: Router,
    private homeworkService: HomeworkService,
    private route: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    sessionStorage.removeItem('tryNumber');
    sessionStorage.removeItem('currentHomework');
    if (!this.pupilDataService.getCurrentSubject()) {
      let subject = sessionStorage.getItem('currentSubject');
      if (subject) {
        this.pupilDataService.setCurrentSubject(JSON.parse(subject));
      }
    }
    this.subject = this.pupilDataService.getCurrentSubject();
    this.pupilId = Number(this.route.snapshot.paramMap.get('id'));
    this.homeworkService.getHomeworksByPupilAndSubject(this.pupilId, this.subject).subscribe(homeworks => {
      this.homeworks = homeworks;
      this.getCompletedHomeworks();
    });
  }

  getCompletedHomeworks() {
    if (this.pupilId) {
      this.pupilService.getCompletedHomeworks(this.pupilId).subscribe(homeworks => {
        for (let homework in homeworks) {
          if (this.homeworks) {
            let curHW = this.homeworks.find(curHomework => curHomework.id == parseInt(homework) && curHomework.subject == this.subject);
            if (curHW) {
              //@ts-ignore
              this.completedHomeworks[parseInt(homework)] = homeworks[parseInt(homework)];
            }
          }
        }
      });
    }
  }

  getHomeworkById(id: string) {
    if (this.homeworks) {
      return this.homeworks.filter(homework => {
        if (homework.id == parseInt(id)) {
          return homework;
        }
        return null;
      })[0].name;
    }
    return null;
  }

  submit(homework: Homework) {
    homework.lastAttempt++;
    this.router.navigate([`pupil/${this.pupilId}/homework/${homework.id}`]);
  }

  submitCompletedHomeworks(tryNumber: number, homeworkId: string) {
    sessionStorage.setItem('tryNumber', JSON.stringify(tryNumber));
    sessionStorage.setItem('currentHomework', homeworkId)
    this.router.navigate([`pupil/${this.pupilId}/homework/${homeworkId}/statistic`]);
  }
}
