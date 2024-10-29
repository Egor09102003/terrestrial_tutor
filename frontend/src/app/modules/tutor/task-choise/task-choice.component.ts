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
import {map} from "rxjs";
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

  ngOnInit(): void {
    if (this.tutorDataService.getHomework()) {
      this.homework = this.tutorDataService.getHomework();
      this.subject = this.homework?.subject;
      this.setTasks();
    } else {
      let homework: number = Number(this.route.snapshot.paramMap.get('hwId'));
      this.tutorService.getHomework(homework).subscribe(homework => {
        this.homework = homework;
        this.subject = this.homework?.subject;
        this.setTasks();
      });
    }
  }

  setTasks() {
    this.allTasks = [];
    for (let i = 0; i < this.tasks.length; i++) {
      if (this.homework != null && this.homework.tasks.some(task => task.id == this.tasks[i].id)) {
        this.allTasks.push(new TaskSelect(this.tasks[i], true));
      } else {
        this.allTasks.push(new TaskSelect(this.tasks[i]));
      }
      this.isCollapsed.push(true);
    }
  }

  checkImage(file: string): boolean {
    return file.endsWith('.jpg') || file.endsWith('.png') || file.endsWith('.jpeg');
  }

  codemirrorInit() {
    if (this.codemirror != undefined) {
      this.codemirror.codeMirror?.refresh();
    }
  }

  getSelectedTasks(): Task[] {
    let selectedTasksIds = this.homework?.tasks.map(task => task.id);

    for (let task of this.allTasks) {
      if (task.isSelected && selectedTasksIds && !selectedTasksIds.includes(task.task.id)) {
        selectedTasksIds.push(task.task.id);
      }
      if (!task.isSelected && selectedTasksIds && selectedTasksIds.includes(task.task.id)) {
        delete selectedTasksIds[selectedTasksIds.indexOf(task.task.id)];
      }
    }

    let currentTasks: Task[] = [];
    if (selectedTasksIds) {
      for (let taskId of selectedTasksIds) {
        let task = this.allTasks.find(task => task.task.id == taskId);
        if (task) {
          currentTasks.push(task.task);
        }
      }
    }
    return currentTasks;
  }

  submit() {
    if (this.homework != null) {
      let currentTasks = this.getSelectedTasks();
      let newTasksList: { [key: number]: string; } = {};
      let tasks = []
      for (let i = 0; i < currentTasks.length; i++) {
        tasks.push(currentTasks[i]);
        if (!this.homework.tasksCheckingTypes[currentTasks[i].id]) {
          newTasksList[currentTasks[i].id] = 'INSTANCE';
        } else {
          newTasksList[currentTasks[i].id] = this.homework.tasksCheckingTypes[currentTasks[i].id];
        }
      }
      this.homework.tasksCheckingTypes = newTasksList;
      this.homework.tasks = tasks;
      this.tutorDataService.setHomework(this.homework);
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
