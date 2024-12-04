import {Component, OnInit} from '@angular/core';
import { PupilDataService } from '../services/pupil.data.service';
import { ActivatedRoute, Router } from '@angular/router';
import { PupilService } from '../services/pupil.service';
import { Homework } from 'src/app/models/Homework';
import {FormBuilder, FormGroup } from "@angular/forms";
import {HomeworkAnswers} from "../../../models/HomeworkAnswers";
import {Task} from "../../../models/Task";
import { EnvironmentService } from 'src/environments/environment.service';
import { HomeworkService } from '../../homework/services/homework.service.';

@Component({
    selector: 'app-homeworks.displaying',
    templateUrl: './homeworks.displaying.component.html',
    styleUrls: ['./homeworks.displaying.component.css']
})
export class HomeworksDisplayingComponent implements OnInit {

  pupilId: number;
  homework: Homework = new Homework;
  tasksAnswers: FormGroup = this.fb.group({});
  pageLoaded = false;
  statistic: HomeworkAnswers = {answersStatuses: {}, attemptCount: 1, ordering: []};
  tasksStatus: {[key: number]: number} = {};

  constructor(private pupilDataService: PupilDataService,
      private pupilService: PupilService,
      private homeworkService: HomeworkService,
      private router: Router,
      private fb: FormBuilder,
      public env: EnvironmentService,
      private route: ActivatedRoute,
    ) {}

  ngOnInit(): void {
      let hwId = Number(this.route.snapshot.paramMap.get('hwId'));
      this.pupilId = Number(this.route.snapshot.paramMap.get('id'));
      this.homeworkService.getHomeworkByIdForPupil(hwId).subscribe(homework => {
        this.homework = homework;
        this.formFill();
      });
      window.addEventListener('beforeunload', this.prevent);
  }

  prevent(event: Event) {
    event.preventDefault();
  }

  formFill() {
    if ('id' in this.homework && this.homework.id) {
      this.homeworkService.initLastAttempt(this.homework.id).subscribe(answers => {
        let answersMap = <HomeworkAnswers> answers
        for (let task of this.homework.tasks) {
          let key = task.id.toString();
          let value = answersMap.answersStatuses[task.id].currentAnswer ?? '';
          this.tasksAnswers.addControl(
            key,
            this.fb.control(value, [])
          );
        }
        this.pageLoaded = true;
      })
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
    if ('id' in this.homework && this.homework.id) {
      this.homeworkService.finishHomework(this.createCheckRequest(), this.homework.id).subscribe(answers => {
        if (answers) {
          this.statistic = <HomeworkAnswers> answers;
          this.router.navigate([`/pupil/${this.pupilId}/homework/${this.homework?.id}/statistic`]).then();
        }
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
    let fileExt = file.substring(file.lastIndexOf('.') + 1).toLowerCase();
    return fileExt === 'png'
      || fileExt === 'jpg'
      || fileExt === 'svg';
  }

  momentCheck(task: Task) {
    this.tasksStatus[task.id] = 0;
    if ('id' in this.homework && this.homework.id) {
      this.homeworkService.saveHomework(this.createCheckRequest(task.id), this.homework.id).subscribe((statuses) => {
        let statusMap = <HomeworkAnswers> statuses;
        for (let status in statusMap.answersStatuses) {
          this.tasksStatus[task.id] = statusMap.answersStatuses[task.id].status === 'RIGHT' ? 1 : 2;
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

  initTable(taskId: number) {
    return this.tasksAnswers.controls[taskId.toString()].value;
  }

  ngOnDestroy() {
    window.addEventListener('beforeunload', this.prevent);
  }
}
