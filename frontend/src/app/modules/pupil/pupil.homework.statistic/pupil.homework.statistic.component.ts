import {Component} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {Task} from "../../../models/Task";
import {EnvironmentService} from 'src/environments/environment.service';
import {Homework} from 'src/app/models/Homework';
import {HomeworkService} from '../../homework/services/homework.service.';
import {StatisticService} from '../../homework/services/statistic.service';
import {AttemptService} from "../../homework/services/attempt.service";
import {Attempt} from "../../../models/Attempt";

@Component({
  selector: 'app-pupil.homework.statistic',
  templateUrl: './pupil.homework.statistic.component.html',
  styleUrl: './pupil.homework.statistic.component.css'
})
export class PupilHomeworkStatisticComponent {

  homework: Homework;
  tasks: Task[] = [];
  pageLoaded = false;
  attempt: Attempt;

  constructor(
              private route: ActivatedRoute,
              private homeworkService: HomeworkService,
              public env: EnvironmentService,
              private statisticService: StatisticService,
              private attemptService: AttemptService,
            ) {}

  ngOnInit(): void {
    let homeworkId = Number(this.route.snapshot.paramMap.get('hwId'));
    this.homeworkService.getHomeworkById(homeworkId).subscribe(homework => {
      this.homework = homework

      this.attemptService.getLastFinishedAttempt(this.homework.id).subscribe(attempt => {
        this.attempt = attempt
      });
    });
  }

    protected readonly Number = Number;
}
