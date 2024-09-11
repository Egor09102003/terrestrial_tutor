import {Component} from '@angular/core';
import {PupilService} from "../services/pupil.service";
import {ActivatedRoute, Router} from "@angular/router";
import {Pupil} from "../../../models/Pupil";
import {HomeworkAnswers} from "../../../models/HomeworkAnswers";
import {Task} from "../../../models/Task";
import {EnvironmentService} from 'src/environments/environment.service';
import {Homework} from 'src/app/models/Homework';
import {HomeworkService} from '../../homework/services/homework.service.';
import { StatisticService } from '../../homework/services/statistic.service';

@Component({
  selector: 'app-pupil.homework.statistic',
  templateUrl: './pupil.homework.statistic.component.html',
  styleUrl: './pupil.homework.statistic.component.css'
})
export class PupilHomeworkStatisticComponent {

  homework: Homework;
  pupilAnswers: HomeworkAnswers;
  tasks: Task[] = [];
  statistic = {
    percent: 0,
    points: 0,
    pointsMax: 0,
  };
  pageLoaded = false;
  attempts: number[] = [];
  rightAnswers: {[key: number]: string};

  constructor(
              private route: ActivatedRoute,
              private homeworkService: HomeworkService,
              public env: EnvironmentService,
              private statisticService: StatisticService,
            ) {}

  ngOnInit(): void {
    let homeworkId = Number(this.route.snapshot.paramMap.get('hwId'));
    this.statisticService.init(homeworkId);
    this.statisticService.pageLoaded.subscribe(pageLoaded => {
      this.homework = this.statisticService.homework;
      this.pupilAnswers = this.statisticService.pupilAnswers;
      this.tasks = this.statisticService.tasks;
      this.attempts = this.statisticService.attempts;
      this.rightAnswers = this.statisticService.rightAnswers;
      this.pageLoaded = true;
    })
  }

  ngOnDestroy() {
    this.statisticService.attempts = [];
  }
}
