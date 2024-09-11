import { Injectable } from '@angular/core';
import { Homework } from 'src/app/models/Homework';
import { HomeworkAnswers } from 'src/app/models/HomeworkAnswers';
import { HomeworkService } from './homework.service.';
import { EnvironmentService } from 'src/environments/environment.service';
import {Task} from "../../../models/Task";
import { Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class StatisticService {

  pageLoaded = new Subject();
  attempts: number[] = [];
  homework: Homework;
  pupilAnswers: HomeworkAnswers;
  tasks: Task[] = [];
  rightAnswers: {[key: number]: string};

  constructor(
    private homeworkService: HomeworkService,
    public env: EnvironmentService,
  ) {
    this.rightAnswers = [];
  }

  init(homeworkId: number, pupilId?: number) {
    this.homeworkService.getHomeworkById(homeworkId).subscribe(homework => {
      this.homework = homework;
      this.tasks = homework.tasks;
      
      this.homeworkService.getLastAttemptAnswers(homeworkId, pupilId).subscribe(pupilAnswers => {
        let pupilAnswersMap = <HomeworkAnswers> pupilAnswers;
        this.pupilAnswers = pupilAnswersMap;
        for (let i = 1; i <= pupilAnswersMap.attemptCount; i++) {
          this.attempts.push(i);
        }
        this.homeworkService.getHomeworkRightAnswers(homeworkId).subscribe(rightAnswers => {
          rightAnswers = <{[key: number]: string}> rightAnswers;
          this.rightAnswers = rightAnswers;
          for (let taskId of Object.keys(rightAnswers)) {
            this.rightAnswers[Number(taskId)] = (JSON.parse(rightAnswers[taskId]));
          }
          this.pageLoaded.next(true);
        });
      });
    });
  }

}
