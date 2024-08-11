import { Component } from '@angular/core';
import {PupilService} from "../services/pupil.service";
import {Router} from "@angular/router";
import {PupilDataService} from "../services/pupil.data.service";
import {Pupil} from "../../../models/Pupil";
import {DetailsAnswer, HomeworkAnswers} from "../../../models/HomeworkAnswers";
import {Task} from "../../../models/Task";

@Component({
  selector: 'app-pupil.homework.statistic',
  templateUrl: './pupil.homework.statistic.component.html',
  styleUrl: './pupil.homework.statistic.component.css'
})
export class PupilHomeworkStatisticComponent {

  currentHomework: number | null = null;
  pupil: Pupil | null = null;
  homeworkAnswers: HomeworkAnswers | null = null;
  tasks: Task[] | null = null;
  checkingAnswers: {[key: string]: DetailsAnswer} = {};
  tryNumber: number[] = [1];
  currentTry: number = 1;

  constructor(private pupilService: PupilService,
              private router: Router,
              private pupilDataService: PupilDataService,) {}

  ngOnInit(): void {
    let  serviceHomework = this.pupilDataService.getCurrentHomework()?.id;
    let storageTry = sessionStorage.getItem('tryNumber');
    if (storageTry) {
      for (let i = 2; i <= parseInt(storageTry); i++) {
        this.tryNumber.push(i);
      }
      this.currentTry = parseInt(storageTry);
    }
    if (serviceHomework) {
      this.currentHomework = serviceHomework;
    } else {
      let homeworkId = sessionStorage.getItem('currentHomework');
      if (homeworkId) {
        this.currentHomework = parseInt(homeworkId);
      }
    }
    if (this.pupilDataService.getPupil()) {
      this.pupil = this.pupilDataService.getPupil();
      this.getStatistic();
    } else if (sessionStorage.getItem('currentPupil')) {
      this.pupilService.getPupilById(sessionStorage.getItem('currentPupil')).subscribe(pupil => {
        this.pupil = pupil;
        this.pupilDataService.setPupil(pupil);
        this.getStatistic();
      });
    } else {
      this.pupilService.getCurrentUser().subscribe(pupil => {
        this.pupil = pupil;
        this.pupilDataService.setPupil(pupil);
        this.getStatistic();
      });
    }
  }

  getStatistic() {
    if (this.pupil) {
      let pupilId = this.pupil.id;
      if (this.currentHomework && pupilId) {
        this.pupil.homeworks.forEach(homework => {
          if (homework.id == this.currentHomework) {
            this.tasks = homework.tasks;
          }
        })
        let attempt = this.currentTry;
        if (attempt) {
          this.pupilService.getHomeworkAnswers(this.currentHomework, pupilId, attempt).subscribe(answers => {
            this.homeworkAnswers = answers;
            if (answers.checkingAnswers) {
              this.checkingAnswers = answers.checkingAnswers;
            }
          });
        }

      }
    }
  }

  getTaskForPrint(id: string) {
    let taskId = parseInt(id);
    if (this.tasks && taskId) {
      for (let task of this.tasks) {
        if (task.id == taskId) {
          return task;
        }
      }
    }
    return null;
  }

  getAnswersStatistic(result: any, id: string) {
    return {pupilAnswer: result.pupilAnswer, rightAnswer: result.rightAnswer}
  }

  getAnswerStatus(pupilAnswer: string, rightAnswer: string) {
    return pupilAnswer.trim().toLowerCase() == rightAnswer.trim().toLowerCase() ? "green" : "red"
  }

  getResultProgress() {
    let percent = 0;
    // let points: number = 0;
    for (let task in this.checkingAnswers) {
      if (this.checkingAnswers) {
        let pupilAnswer = this.checkingAnswers[task].pupilAnswer;
        let rightAnswer = this.checkingAnswers[task].rightAnswer;
        if (this.getAnswerStatus(pupilAnswer ? pupilAnswer: '', rightAnswer) == 'green' ? 1 : 0) {
          percent += 1;
          // points += this.tasks?.find((curTask: Task) => curTask.id.toString() === task).cost;
        }
      }
    }
    if (this.tasks) {
      return (percent / this.tasks?.length * 100).toFixed(2);
    }
    return 0;
  }

  setCurrentAttempt(attempt: number) {
    this.currentTry = attempt;
    this.getStatistic();
  }

  checkFilesAvailability(task: Task | null) {
    let filesAmount = 0;
    if (task && task.files) {
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

  submit() {
    this.router.navigate(['pupil']);
  }

  autoLink(value: string | undefined): string {
    const urlRegex = /(https?:\/\/\S+)/g;
    return value ? value.replace(urlRegex, '<a href="$1" target="_blank">$1</a>') : "";
  }

  decodeTable(table: string | undefined) {
    let parsedTable: [[string]] = JSON.parse(table ? table : '');
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
