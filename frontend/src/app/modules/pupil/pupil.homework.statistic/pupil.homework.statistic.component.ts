import {Component} from '@angular/core';
import {PupilService} from "../services/pupil.service";
import {ActivatedRoute, Router} from "@angular/router";
import {Pupil} from "../../../models/Pupil";
import {HomeworkAnswers} from "../../../models/HomeworkAnswers";
import {Task} from "../../../models/Task";
import {EnvironmentService} from 'src/environments/environment.service';
import {Homework} from 'src/app/models/Homework';
import {HomeworkService} from '../../homework/services/homework.service.';

@Component({
  selector: 'app-pupil.homework.statistic',
  templateUrl: './pupil.homework.statistic.component.html',
  styleUrl: './pupil.homework.statistic.component.css'
})
export class PupilHomeworkStatisticComponent {

  homework: Homework;
  pupil: Pupil | null = null;
  pupilAnswers: HomeworkAnswers;
  rightAnswers: {[key: number]: string};
  tasks: Task[] = [];
  statistic = {
    percent: 0,
    points: 0,
    pointsMax: 0,
  };
  pageLoaded = false;
  attempts: number[] = [];

  constructor(private pupilService: PupilService,
              private router: Router,
              private route: ActivatedRoute,
              private homeworkService: HomeworkService,
              public env: EnvironmentService,
            ) {}

  ngOnInit(): void {
    let homeworkId = Number(this.route.snapshot.paramMap.get('hwId'));
    this.homeworkService.getHomeworkById(homeworkId).subscribe(homework => {
      this.homework = homework;
      this.tasks = homework.tasks;
      this.homeworkService.getHomeworkRightAnswers(homeworkId).subscribe(rightAnswers => {
        rightAnswers = <{[key: number]: string}> rightAnswers;
        this.rightAnswers = rightAnswers;
        for (let taskId of Object.keys(rightAnswers)) {
          this.rightAnswers[Number(taskId)] = (JSON.parse(rightAnswers[taskId]));
        }
        this.homeworkService.getLastAttemptAnswers(homeworkId).subscribe(pupilAnswers => {
          let pupilAnswersMap = <HomeworkAnswers> pupilAnswers;
          this.pupilAnswers = pupilAnswersMap;
          for (let i = 1; i <= pupilAnswersMap.attemptCount; i++) {
            this.attempts.push(i);
          }
          this.getResultProgress();
          this.pageLoaded = true;
        });
      })
    });
  }

  getTaskForPrint(id: number) {
    if (this.tasks && id) {
      for (let task of this.tasks) {
        if (task.id == id) {
          return task;
        }
      }
    }
    return null;
  }


  getResultProgress() {
    let percent = 0;
    let points = 0;
    let pointsMax = 0;

    for (let taskId in this.pupilAnswers.answersStatuses) {
      let task = this.tasks.find((curTask: Task) => curTask.id.toString() === taskId);
      if (this.pupilAnswers.answersStatuses[taskId].status === 'RIGHT') {
        percent += 1;
        if (task) {
          points += task.cost ?? 1;
        }
      }
      pointsMax += task?.cost ?? 1;
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
    this.pageLoaded = false;
    if ('id' in this.homework && this.homework.id) {
      this.homeworkService.getAttemptAnswers(this.homework.id, attempt).subscribe(pupilAnswers => {
        this.pupilAnswers = <HomeworkAnswers>pupilAnswers;
        this.getResultProgress();
        this.pageLoaded = true;
      });
    }
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

  checkAnswer(task: Task) {
    if (this.pupilAnswers.answersStatuses[task.id]) {
      return this.pupilAnswers.answersStatuses[task.id].status === 'RIGHT' ? task.cost : 0;
    }
    return 0;
  }
}
