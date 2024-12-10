import {Task} from "../../../models/Task";
import { ChangeDetectionStrategy, Component, Input, Output, type OnInit, EventEmitter, SimpleChange } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { PupilService } from "../../pupil/services/pupil.service";
import { HomeworkService } from "../services/homework.service.";
import { EnvironmentService } from "src/environments/environment.service";
import { Homework } from "src/app/models/Homework";
import { TaskService } from "../../task/services/task.service";
import {Attempt} from "../../../models/Attempt";
import {AttemptService} from "../services/attempt.service";

@Component({
    selector: 'statistic-container',
    templateUrl: './statistic.container.component.html',
    styleUrl: './statistic.container.component.css',
})
export class StatisticContainerComponent implements OnInit {

    @Output() updatedAttempt = new EventEmitter<Attempt>
    @Input() pageLoaded: boolean;
    @Input() homework: Homework;
    @Input() pupilId?: number;
    @Input() attempt: Attempt;
    protected attemptNumbers: number[] = [];
    statistic = {
        percent: 0,
        points: 0,
        pointsMax: 0,
    };

    constructor(private router: Router,
        private attemptService: AttemptService,
        public env: EnvironmentService,
      ) {}

    ngOnInit(): void {

    }

    submit() {
        this.router.navigate(['/']);
    }

    updateStatistic() {
      this.statistic = {
        percent: 0,
        points: 0,
        pointsMax: 0,
      };
      this.statistic.points = this.attempt.attemptPoints;
      let rightAnswersAmount = 0;
      for (let taskId in this.homework.taskChecking) {
        this.statistic.pointsMax += this.homework.taskChecking[taskId].task.cost ?? 0;
        rightAnswersAmount += taskId in this.attempt.answers && this.attempt.answers[taskId].status === 'RIGHT' ? 1 : 0;
      }
      this.statistic.percent = rightAnswersAmount / Object.keys(this.homework.taskChecking).length * 100;
    }

    updateAttempt(attemptNumber: number) {
      this.attemptService.getAttemptByNumber(attemptNumber, this.homework.id).subscribe(attempt => {
        this.attempt = attempt;
        this.updatedAttempt.emit(attempt);
        this.updateStatistic();
      })
    }

    ngOnChanges(changes: SimpleChange) {
      if ('pupilAnswers' in changes && !(<SimpleChange>changes.pupilAnswers).firstChange) {
        // this.getResultProgress();
      }
      if (this.attempt) {
        this.attemptNumbers = [];
        for (let i = 1; i <= this.attempt.attemptNumber; i++) {
          this.attemptNumbers.push(i);
        }
        this.updateStatistic();
      }
    }
}
