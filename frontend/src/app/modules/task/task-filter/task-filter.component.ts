import { Component, Input, Output, EventEmitter, SimpleChange } from '@angular/core';
import { TaskFilters } from 'src/app/models/enums/TaskFilters';
import {Task} from "../../../models/Task";
import { TaskService } from '../services/task.service';
import { FormControl, FormGroup } from '@angular/forms';
import { Subject } from 'src/app/models/Subject';
import { SubjectsService } from '../../subjects/services/subjects.service';

@Component({
  selector: 'task-filter',
  templateUrl: './task-filter.component.html',
  styleUrl: './task-filter.component.css'
})
export class TaskFilterComponent {

  tasksUpload: boolean = false;
  pageSize: number = 30;
  @Output() total = new EventEmitter<number>;
  @Output() tasks = new EventEmitter<Task[]>;
  @Output() pageLoaded = new EventEmitter<boolean>(false);
  @Input() initialFilters: {[key: string]: string} = {};
  @Input() bannedFilters: string[] = []
  @Input() filters: {[key: string]: string} = {};
  @Input() page: number = 1;
  filterForm: FormGroup = new FormGroup({});
  subjects: Subject[] = [];


  protected readonly taskFilters = TaskFilters;
  protected readonly Object = Object;

  constructor(
    private taskService: TaskService,
    private subjectService: SubjectsService
  ) {}

  ngOnInit() {
    this.filters = this.initialFilters;
    for (let filter of Object.keys(this.taskFilters)) {
      this.filterForm.addControl(
        filter,
        new FormControl(this.filters[filter] ?? '')
      )
    }
    this.subjectService.getAllSubjects().subscribe(subjects => {
      this.subjects = subjects;
      this.updateData(1);
      this.pageLoaded.emit(true);
    });
  }

  updateData(page: number) {
    this.taskService.getAllTasks(page, 30, this.filterForm.value).subscribe(data => {
      this.total.emit(data.total);
      this.tasks.emit(data.tasks);
      this.tasksUpload = true;
      window.scroll({
        top: 0,
        left: 0,
        behavior: 'smooth'
      });
    });
  }

  ngOnChanges(changes: SimpleChange) {
    if ('page' in changes) {
      this.updateData(this.page);
    }
  }

}
