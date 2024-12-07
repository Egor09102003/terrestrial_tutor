import {Component, OnInit, ViewChild, ViewEncapsulation} from '@angular/core';
import {Task} from "../../../models/Task";
import {CodemirrorComponent} from "@ctrl/ngx-codemirror";
import {ActivatedRoute, Router} from "@angular/router";
import {Homework} from "../../../models/Homework";
import {answerTypes} from "../../../models/AnswerTypes";
import {HomeworkService} from "../../homework/services/homework.service.";
import {TaskChecking} from "../../../models/TaskChecking";

@Component({
  selector: 'app-task-choise',
  templateUrl: './task-choice.component.html',
  styleUrls: ['./task-choice.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class TaskChoiceComponent implements OnInit {

  @ViewChild('codemirrorComponent') codemirror: CodemirrorComponent | undefined;

  constructor(
              private homeworkService : HomeworkService,
              private router: Router,
              private route: ActivatedRoute,
  ) { }

  subject: any;
  pageLoaded: boolean = false;
  homework: Homework;
  page = 1;
  pageSize = 30;
  maxSize = 100;
  selectedTasks: {[key: number]: Task} = {};
  tasks: Task[] = [];

  ngOnInit(): void {
    let homework: number = Number(this.route.snapshot.paramMap.get('hwId'));
    this.homeworkService.getHomeworkById(homework).subscribe(homework => {
      this.homework = homework;
      this.subject = this.homework.subject;
      this.setSelectedTasks();
    });
  }

  select(event: any, task: Task) {
    if (event.target.checked) {
      this.selectedTasks[task.id] = task;
    } else {
      delete this.selectedTasks[task.id];
    }
  }

  setSelectedTasks() {
    if (this.homework) {
      for (let taskId in this.homework.taskChecking) {
        this.selectedTasks[taskId] = this.homework.taskChecking[taskId].task;
      }
    }
  }

  submit() {
    console.log(this.homework)
    if (this.homework != null) {
      let orderIndex = 0;
      for (let taskId in this.homework.taskChecking) {
        if (this.homework.taskChecking[Number(taskId)].orderIndex > orderIndex) {
          orderIndex = this.homework.taskChecking[Number(taskId)].orderIndex;
        }
      }
      for (let key of Object.keys(this.selectedTasks)) {
        let taskId = Number(key);
        if (!this.homework.taskChecking[taskId]) {
          let checking = new TaskChecking();
          checking.orderIndex = ++orderIndex;
          checking.checkingType = 'INSTANCE';
          this.homework.taskChecking[taskId] = checking
        }
      }
      console.log(this.homework)
      this.pageLoaded = false;
      this.homeworkService.saveHomework(this.homework).subscribe(homework => {
        this.pageLoaded = true;
        let tutorId = this.route.snapshot.paramMap.get('id');
        this.router.navigate([`/tutor/${tutorId}/constructor/${homework.id}`], {
          queryParamsHandling: 'merge'
        });
      });
    }
  }

  protected readonly answerTypes = answerTypes;
}
