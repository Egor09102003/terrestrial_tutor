import { Component } from '@angular/core';
import { PupilDataService } from '../services/pupil.data.service';
import { Router } from '@angular/router';
import { PupilService } from '../services/pupil.service';
import { Pupil } from 'src/app/models/Pupil';
import { Homework } from 'src/app/models/Homework';
import { TutorService } from '../../tutor/services/tutor.service';
import {FormBuilder, FormGroup } from "@angular/forms";
import {HomeworkAnswers} from "../../../models/HomeworkAnswers";
import {Task} from "../../../models/Task";
import { EnvironmentService } from 'src/environments/environment.service';

@Component({
    selector: 'app-homeworks.displaying',
    templateUrl: './homeworks.displaying.component.html',
    styleUrls: ['./homeworks.displaying.component.css']
})
export class HomeworksDisplayingComponent {

  pupil: Pupil | null = null;
  homework: Homework | null = null;
  // @ts-ignore
  tasksAnswers: FormGroup = this.fb.group({});
  pageLoaded = false;
  statistic: HomeworkAnswers = {checkingAnswers: {}, attemptCount: 1};
  tasksStatus: {[key: string]: number} = {};

  constructor(private pupilDataService: PupilDataService,
      private pupilService: PupilService,
      private homeworkService: TutorService,
      private router: Router,
      private fb: FormBuilder,
      public env: EnvironmentService,
    ) {}

  ngOnInit(): void {
    this.pupil = this.pupilDataService.getPupil();
    if (this.pupil) {
      this.homework = this.pupilDataService.getCurrentHomework();
      if (this.homework) {
        this.formFill();
        this.pageLoaded = true;
      }
    } else {
      this.pupilService.getCurrentUser().subscribe(pupil => {
        this.pupil = pupil;
        this.pupilDataService.setPupil(pupil);
        let homework = this.pupil?.homeworks.find(homework => {
          return homework.id == Number(sessionStorage.getItem('currentHomework'));
        });

        if (homework) {
          this.pupilDataService.setCurrentHomework(homework);
          this.homework = homework;
          this.formFill();
          this.pageLoaded = true;
        }
      });
    }
  }

  formFill() {
    // @ts-ignore
    for (let task of this.homework?.tasks) {
      let key = task.id.toString();
      this.tasksAnswers?.addControl(
        key,
        this.fb.control('', [])
      );
    }
  }

  createCheckRequest(taskId = -1) {
    let answers: {[key: number]: string} = {};
    if (taskId != -1) {
      answers[taskId] = this.tasksAnswers.controls[taskId].value;
    } else {
      for (let control in this.tasksAnswers.controls) {
        answers[parseInt(control)] = this.tasksAnswers.controls[control].value;
      }
    }
    return answers;
  }

  submit() {
    if (this.homework?.id) {
      if (this.homework.lastAttempt == 0) this.homework.lastAttempt = 1;
      this.pupilService.sendAnswers(this.createCheckRequest(), this.homework?.id, this.homework.lastAttempt).subscribe((homework) => {
        if (homework) {
          this.statistic = <HomeworkAnswers>homework;
        }
        sessionStorage.setItem('tryNumber', String(this.statistic.attemptCount));
        this.router.navigate([`/pupil/${this.pupil?.id}/homework/${this.homework?.id}/statistic`]);
      });
    }
  }

  checkFilesAvailability(task: Task) {
    let filesAmount = 0;
    if (task.files) {
      for (let file of task.files) {
        if (!this.checkImage(file)) {
          filesAmount++;
        }
      }
      return filesAmount > 0;
    }
    return false;
  }

  checkImage(file: string): boolean {
    return file.endsWith('.jpg') || file.endsWith('.png') || file.endsWith('.jpeg');
  }

  momentCheck(taskId: number) {
    this.tasksStatus[taskId.toString()] = 0;
    if (this.homework?.id) {
      if (this.homework.lastAttempt == 0) this.homework.lastAttempt = 1;
      // @ts-ignore
      this.pupilService.sendAnswers(this.createCheckRequest(taskId), this.homework?.id, this.homework.lastAttempt).subscribe((homework) => {
        if (homework) {
          this.statistic = <HomeworkAnswers>homework;
          let currentTaskStatistic = this.statistic.checkingAnswers[taskId.toString()];
          if (currentTaskStatistic) {
            this.tasksStatus[taskId.toString()] = currentTaskStatistic.pupilAnswer == currentTaskStatistic.rightAnswer ? 1 : 2;
          }
        }
      });
    }
  }

  checkChecking(taskId: number) {
    if (this.homework?.tasksCheckingTypes[taskId]) {
      return this.homework?.tasksCheckingTypes[taskId] == 'INSTANCE';
    }
    return false;
  }

  autoLink(value: string): string {
    const urlRegex = /(https?:\/\/\S+)/g;
    return value.replace(urlRegex, '<a href="$1" target="_blank">$1</a>');
  }

  decodeTable(table: string) {
    let parsedTable: [[string]] = JSON.parse(table);
    for (let i = 0; i < parsedTable.length; i++) {
      for (let j = 0; j < parsedTable[i].length; j++) {
        if (parsedTable[i][j] != '') {
          return parsedTable;
        }
      }
    }
    return null;
  }
}
