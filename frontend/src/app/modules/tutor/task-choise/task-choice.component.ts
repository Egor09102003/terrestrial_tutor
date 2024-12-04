import {Component, OnInit, ViewChild, ViewEncapsulation} from '@angular/core';
import {dataService} from "../services/data.service";
import {TaskService} from "../../task/services/task.service";
import {Task} from "../../../models/Task";
import {TaskSelect} from "../../../models/TaskSelect";
import {CodemirrorComponent} from "@ctrl/ngx-codemirror";
import {TutorService} from "../services/tutor.service";
import {ActivatedRoute, Router} from "@angular/router";
import {Homework} from "../../../models/Homework";
import {Store} from "@ngrx/store";
import {TutorDataService} from "../storage/tutor.data.service";
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

  constructor(private dataService : dataService,
              private taskService : TaskService,
              private tutorService : TutorService,
              private router: Router,
              private store: Store,
              private tutorDataService: TutorDataService,
              private route: ActivatedRoute,
  ) { }

  allTasks: TaskSelect[] = [];
  tasks: Task[] = [];
  subject: any;
  pageLoaded: boolean = false;
  isCollapsed: boolean[] = [];
  homework: Homework | null = null;
  filterText = new UntypedFormControl('');
  page = 1;
  pageSize = 30;
  maxSize = 100;
  selectedTasks: {[key: number]: Task} = {};

  ngOnInit(): void {
    if (this.tutorDataService.getHomework()) {
      this.homework = this.tutorDataService.getHomework();
      this.subject = this.homework?.subject;
      this.setSelectedTasks();
    } else {
      let homework: number = Number(this.route.snapshot.paramMap.get('hwId'));
      this.tutorService.getHomework(homework).subscribe(homework => {
        this.homework = homework;
        this.subject = this.homework?.subject;
        this.setSelectedTasks();
      });
    }
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
      for (let task of this.homework.tasks) {
        this.selectedTasks[task.id] = task;
      }
    }
  }

  checkImage(file: string): boolean {
    let fileExt = file.substring(file.lastIndexOf('.') + 1).toLowerCase();
    return fileExt === 'png'
      || fileExt === 'jpg'
      || fileExt === 'svg';
  }

  codemirrorInit() {
    if (this.codemirror != undefined) {
      this.codemirror.codeMirror?.refresh();
    }
  }

  submit() {
    if (this.homework != null) {
      for (let task of Object.values(this.selectedTasks)) {
        if (!this.homework.tasksCheckingTypes[task.id]) {
          this.homework.tasksCheckingTypes[task.id] = 'INSTANCE';
        }
      }
      this.homework.tasks = Object.values(this.selectedTasks);
      this.pageLoaded = false;
      this.tutorService.saveHomework(this.homework).subscribe(homework => {
        this.pageLoaded = true;
        let tutorId = this.route.snapshot.paramMap.get('id');
        this.router.navigate([`/tutor/${tutorId}/constructor/${homework.id}`], {
          queryParamsHandling: 'merge'
        });
      });
    }
  }

  protected readonly event = event;
  protected readonly answerTypes = answerTypes;
}
