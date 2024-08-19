import {Component, OnInit} from '@angular/core';
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
export class HomeworksDisplayingComponent implements OnInit {

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
        let homework;
        if (this.pupil && 'homeworks' in this.pupil)
        homework = this.pupil.homeworks.find(homework => {
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
    if (this.homework && 'id' in this.homework && this.homework.id) {
      if (this.homework.lastAttempt == 0) this.homework.lastAttempt = 1;
      this.pupilService.sendAnswers(this.createCheckRequest(), this.homework.id, this.homework.lastAttempt).subscribe((homework) => {
        if (homework) {
          this.statistic = <HomeworkAnswers>homework;
        }
        sessionStorage.setItem('tryNumber', String(this.statistic.attemptCount));
        this.router.navigate([`/pupil/${this.pupil?.id}/homework/${this.homework?.id}/statistic`]).then();
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

  momentCheck(task: Task) {
    this.tasksStatus[task.id.toString()] = 0;
    if (this.homework && 'id' in this.homework && this.homework.id) {
      if (this.homework.lastAttempt == 0) this.homework.lastAttempt = 1;
      this.pupilService.sendAnswers(this.createCheckRequest(task.id), this.homework?.id, this.homework.lastAttempt).subscribe((homework) => {
        if (homework) {
          this.statistic = <HomeworkAnswers>homework;
          let currentTaskStatistic = this.statistic.checkingAnswers[task.id.toString()];
          if (currentTaskStatistic) {
            if (task.answerType === 'TABLE') {
              this.tasksStatus[task.id.toString()] = this.checkTableAnswer(currentTaskStatistic.pupilAnswer ?? '', currentTaskStatistic.rightAnswer) ? 1 : 2;
            }
            this.tasksStatus[task.id.toString()] = currentTaskStatistic.pupilAnswer == currentTaskStatistic.rightAnswer ? 1 : 2;
          }
        }
      });
    }
  }

  checkTableAnswer (pupilAnswer: string, rightAnswer:string): boolean {
    try {
      let pupilTable = JSON.parse(pupilAnswer);
      let rightTable = JSON.parse(rightAnswer);
      for (let i in pupilTable) {
        for (let j in pupilTable[i]) {
          if (pupilTable[i][j] && rightTable[i][j] && pupilTable[i][j] !== rightTable[i][j]) {
            return false;
          }
          if (pupilTable[i][j] && !rightTable[i][j] && pupilTable[i][j].trim() !== '') {
            return false
          }
        }
      }
    } catch (e) {
      return false;
    }
    return true;
  }

  checkChecking(taskId: number) {
    if (this.homework?.tasksCheckingTypes[taskId]) {
      return this.homework?.tasksCheckingTypes[taskId] == 'INSTANCE';
    }
    return false;
  }

  updateTable(table: string, taskId: number) {
    if (taskId) {
        this.tasksAnswers.controls[taskId.toString()].setValue(table);
    }
  }
}
