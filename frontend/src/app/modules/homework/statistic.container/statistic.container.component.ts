import {Task} from "../../../models/Task";
import { ChangeDetectionStrategy, Component, Input, Output, type OnInit, EventEmitter, SimpleChange } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HomeworkAnswers } from 'src/app/models/HomeworkAnswers';
import { PupilService } from "../../pupil/services/pupil.service";
import { HomeworkService } from "../services/homework.service.";
import { EnvironmentService } from "src/environments/environment.service";
import { Homework } from "src/app/models/Homework";
import { TaskService } from "../../task/services/task.service";

@Component({
    selector: 'statistic-container',
    templateUrl: './statistic.container.component.html',
    styleUrl: './statistic.container.component.css',
})
export class StatisticContainerComponent implements OnInit {

    @Input() pupilAnswers: HomeworkAnswers;
    @Input() attempts: number[] = [];
    @Output() updatedAnswers = new EventEmitter<HomeworkAnswers>
    tasks: Task[];
    @Output() tasksUpdate = new EventEmitter<Task[]>;
    @Input() pageLoaded: boolean;
    attempt: number
    @Input() homework: Homework;
    @Input() pupilId?: number;
    statistic = {
        percent: 0,
        points: 0,
        pointsMax: 0,
      };

    constructor(private router: Router,
        private homeworkService: HomeworkService,
        private taskService: TaskService,
        public env: EnvironmentService,
      ) {}

    ngOnInit(): void {
        this.tasks = this.homework.tasks;   
        this.getResultProgress(); 
    }

    submit() {
        this.router.navigate(['/']);
    }

    getResultProgress() {
        let percent = 0;
        let points = 0;
        let pointsMax = 0;
    
        for (let taskId in this.pupilAnswers.answersStatuses) {
          let task = this.tasks.find((curTask: Task) => curTask.id.toString() === taskId);
          if (this.pupilAnswers.answersStatuses[taskId].status === 'RIGHT') {
            percent += 1;
          }
          points += this.pupilAnswers.answersStatuses[taskId].points ?? 0;
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
          this.homeworkService.getAttemptAnswers(this.homework.id, attempt, this.pupilId).subscribe(pupilAnswers => {
            this.pupilAnswers = <HomeworkAnswers>pupilAnswers;
            let taskIds: number[] = [];
            for (let taskId in pupilAnswers.answersStatuses) {
              taskIds.push(Number(taskId));
            }
            this.taskService.getTaskByIds(taskIds).subscribe(tasks => {
              this.tasks = tasks;
              this.tasksUpdate.emit(tasks);
              this.updatedAnswers.emit(this.pupilAnswers);
              this.getResultProgress();
              this.pageLoaded = true;
            })
          });
        }
    }

    ngOnChanges(changes: SimpleChange) {
      if ('pupilAnswers' in changes && !(<SimpleChange>changes.pupilAnswers).firstChange) {
        this.getResultProgress();
      }
    }
}
