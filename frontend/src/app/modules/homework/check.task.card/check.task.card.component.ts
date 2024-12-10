import { Component, Input, type OnInit } from '@angular/core';
import {Task} from "../../../models/Task";
import { EnvironmentService } from 'src/environments/environment.service';
import { HomeworkService } from '../services/homework.service.';
import { ActivatedRoute } from '@angular/router';
import {Answer} from "../../../models/Answer";
import {AnswerStatuses} from "../../../models/enums/AnswerStatuses";

@Component({
    selector: 'check-task-card',
    templateUrl: './check.task.card.component.html',
    styleUrl: './check.task.card.component.css',
})
export class CheckTaskCardComponent implements OnInit {

  @Input() pupilAnswer: Answer
  @Input() task: Task

  constructor(
      public env: EnvironmentService,
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
    return file.endsWith('.jpg') || file.endsWith('.png') || file.endsWith('.jpeg');
  }

  protected readonly AnswerStatuses = AnswerStatuses;
}
