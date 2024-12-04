import {Component, Input, type OnInit} from '@angular/core';
import {HomeworkAnswers} from 'src/app/models/HomeworkAnswers';
import {Task} from "../../../models/Task";
import {EnvironmentService} from 'src/environments/environment.service';
import {HomeworkService} from '../services/homework.service.';
import {ActivatedRoute} from '@angular/router';

@Component({
    selector: 'check-task-card',
    templateUrl: './check.task.card.component.html',
    styleUrl: './check.task.card.component.css',
})
export class CheckTaskCardComponent implements OnInit {

  @Input() pupilAnswers: HomeworkAnswers
  @Input() task: Task
  @Input() rightAnswers: {[key: number]: string};

  constructor(
      public env: EnvironmentService,
      private homeworkService: HomeworkService,
      private route: ActivatedRoute,
    ) {}

  ngOnInit(): void {
  }

  checkFilesAvailability() {
    let filesAmount = 0;
    if (this.task && this.task.files) {
      for (let file of this.task.files) {
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

  checkAnswer(task: Task) {
    if (this.pupilAnswers.answersStatuses[task.id]) {
      return this.pupilAnswers.answersStatuses[task.id].status === 'RIGHT' ? task.cost : 0;
    }
    return 0;
  }

}
