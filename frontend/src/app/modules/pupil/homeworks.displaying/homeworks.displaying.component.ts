import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Homework} from 'src/app/models/Homework';
import {FormBuilder, FormGroup} from "@angular/forms";
import {Task} from "../../../models/Task";
import {EnvironmentService} from 'src/environments/environment.service';
import {HomeworkService} from '../../homework/services/homework.service.';
import {Attempt} from "../../../models/Attempt";
import {AnswerStatuses} from "../../../models/enums/AnswerStatuses";
import {AttemptService} from "../../homework/services/attempt.service";

@Component({
    selector: 'app-homeworks.displaying',
    templateUrl: './homeworks.displaying.component.html',
    styleUrls: ['./homeworks.displaying.component.css']
})
export class HomeworksDisplayingComponent implements OnInit {

  pupilId: number;
  homework: Homework;
  taskAnswers: FormGroup = this.fb.group({});
  pageLoaded = false;
  attempt: Attempt;
  tasksOrdering: number[];

  constructor(
      private homeworkService: HomeworkService,
      private router: Router,
      private fb: FormBuilder,
      public env: EnvironmentService,
      private route: ActivatedRoute,
      private attemptService: AttemptService,
    ) {}

  ngOnInit(): void {
      let hwId = Number(this.route.snapshot.paramMap.get('hwId'));
      this.pupilId = Number(this.route.snapshot.paramMap.get('id'));
      this.homeworkService.getHomeworkById(hwId).subscribe(homework => {
        this.homework = homework;

        this.tasksOrdering = Object.values(this.homework.taskChecking).sort(
          (checking1, checking2) =>
            checking1.orderIndex > checking2.orderIndex ? 1 : -1
        ).map((checking) => checking.task.id);

        this.formFill();
      });
      window.addEventListener('beforeunload', this.prevent);
  }

  prevent(event: Event) {
    event.preventDefault();
  }

  formFill() {
    if ('id' in this.homework && this.homework.id) {
      this.attemptService.getActiveAttempt(this.homework.id).subscribe(attempt => {
        this.attempt = attempt;
        for (let taskId in this.homework.taskChecking) {
          this.taskAnswers.addControl(
            taskId,
            this.fb.control(this.attempt.answers[taskId] ? this.attempt.answers[taskId].answer : '', [])
          );
        }
        this.pageLoaded = true;
      })
    }
  }

  submit() {
    if ('id' in this.homework && this.homework.id) {
      this.attemptService.finishAttempt(this.taskAnswers.value, this.homework.id).subscribe(answers => {
        if (answers) {
          this.router.navigate([`/pupil/${this.pupilId}/homework/${this.homework?.id}/statistic`]);
        }
      });
    }
  }

  checkFilesAvailability(task: Task) {
    let filesAmount = 0;
    if (task.files) {
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

  momentCheck(task: Task) {
    let answers: {[key: number]: string} = {}
    answers[task.id] = this.taskAnswers.controls[task.id.toString()].value;
    this.attemptService.saveAttempt(
      answers,
      this.homework.id
    ).subscribe(attempt => {
      this.attempt = attempt
    });
  }

  updateTable(table: string, taskId: number) {
    if (taskId) {
        this.taskAnswers.controls[taskId.toString()].setValue(table);
    }
  }

  initTable(taskId: number) {
    return this.taskAnswers.controls[taskId.toString()].value;
  }

  ngOnDestroy() {
    window.addEventListener('beforeunload', this.prevent);
  }

  protected readonly AnswerStatuses = AnswerStatuses;
}
