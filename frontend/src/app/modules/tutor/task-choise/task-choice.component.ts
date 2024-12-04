import {Component, OnInit, ViewChild, ViewEncapsulation} from '@angular/core';
import {TaskService} from "../../task/services/task.service";
import {Task} from "../../../models/Task";
import {TaskSelect} from "../../../models/TaskSelect";
import {CodemirrorComponent} from "@ctrl/ngx-codemirror";
import {TutorService} from "../services/tutor.service";
import {ActivatedRoute, Router} from "@angular/router";
import {Homework} from "../../../models/Homework";
import {UntypedFormControl} from "@angular/forms";
import {answerTypes} from "../../../models/AnswerTypes";

@Component({
  selector: 'app-task-choise',
  templateUrl: './task-choice.component.html',
  styleUrls: ['./task-choice.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class TaskChoiceComponent implements OnInit {

  @ViewChild('codemirrorComponent') codemirror: CodemirrorComponent | undefined;

  constructor(
              private taskService : TaskService,
              private tutorService : TutorService,
              private router: Router,
              private route: ActivatedRoute,
  ) { }

  subject: any;
  pageLoaded: boolean = false;
  homework: Homework;
  page = 1;
  pageSize = 30;
  maxSize = 100;

  ngOnInit(): void {
    let homework: number = Number(this.route.snapshot.paramMap.get('hwId'));
    this.tutorService.getHomework(homework).subscribe(homework => {
      this.homework = homework;
      this.subject = this.homework?.subject;
      this.setSelectedTasks();
    });
  }

  select(event: any, task: Task) {
    // if (event.target.checked) {
    //   this.selectedTasks[task.id] = task;
    // } else {
    //   delete this.selectedTasks[task.id];
    // }
  }

  setSelectedTasks() {
    // if (this.homework) {
    //   for (let task of this.homework.tasks) {
    //     this.selectedTasks[task.id] = task;
    //   }
    // }
  }

  checkImage(file: string): boolean {
    return file.endsWith('.jpg') || file.endsWith('.png') || file.endsWith('.jpeg');
  }

  codemirrorInit() {
    if (this.codemirror != undefined) {
      this.codemirror.codeMirror?.refresh();
    }
  }

  submit() {
    // if (this.homework != null) {
    //   for (let task of Object.values(this.selectedTasks)) {
    //     if (!this.homework.taskChecking[task.id]) {
    //       this.homework.taskChecking[task.id].checkingType = 'INSTANCE';
    //     }
    //   }
    //   this.homework.tasks = Object.values(this.selectedTasks);
    //   this.pageLoaded = false;
    //   this.tutorService.saveHomework(this.homework).subscribe(homework => {
    //     this.pageLoaded = true;
    //     let tutorId = this.route.snapshot.paramMap.get('id');
    //     this.router.navigate([`/tutor/${tutorId}/constructor/${homework.id}`], {
    //       queryParamsHandling: 'merge'
    //     });
    //   });
    // }
  }

  protected readonly event = event;
  protected readonly answerTypes = answerTypes;
}
