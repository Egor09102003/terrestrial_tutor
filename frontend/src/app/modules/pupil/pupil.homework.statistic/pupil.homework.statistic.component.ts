import { Component } from '@angular/core';
import {PupilService} from "../services/pupil.service";
import {Router} from "@angular/router";
import {PupilDataService} from "../services/pupil.data.service";
import {Pupil} from "../../../models/Pupil";
import {DetailsAnswer, HomeworkAnswers} from "../../../models/HomeworkAnswers";
import {Task} from "../../../models/Task";
import { EnvironmentService } from 'src/environments/environment.service';

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
  statistic = {
    percent: 0,
    points: 0,
    pointsMax: 0,
  };

  constructor(private pupilService: PupilService,
              private router: Router,
              private pupilDataService: PupilDataService,
              public env: EnvironmentService,
            ) {}

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
            this.getResultProgress();
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
    let points = 0;
    let pointsMax = 0;
    
    for (let taskId in this.checkingAnswers) {
      if (this.checkingAnswers) {
        let pupilAnswer = this.checkingAnswers[taskId].pupilAnswer;
        let rightAnswer = this.checkingAnswers[taskId].rightAnswer;
        let task = this.tasks?.find((curTask: Task) => curTask.id.toString() === taskId);
        if (this.getAnswerStatus(pupilAnswer ? pupilAnswer: '', rightAnswer) == 'green' ? 1 : 0) {
          percent += 1;
          if (task) {
            points += task.cost ?? 1;
          }
        }
        pointsMax += task?.cost ?? 1;
      }
    }
    if (this.tasks) {
      this.statistic.percent = Number((percent / this.tasks?.length * 100).toFixed(2));
      this.statistic.points = Number(points.toString());
      this.statistic.pointsMax = pointsMax;
    }
  }

  setCurrentAttempt(attempt: number) {
    this.statistic = {
      percent: 0,
      points: 0,
      pointsMax: 0,
    };
    this.getResultProgress();
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

  checkAnswer(task: Task) {
    let answerStatistic = this.checkingAnswers[task.id.toString()];
    return answerStatistic.rightAnswer === answerStatistic.pupilAnswer ? task.cost : 0;
  }

}
