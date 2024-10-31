import { Component, OnInit } from '@angular/core';
import {TokenStorageService} from "../../security/token-storage.service";
import {ActivatedRoute, Router} from "@angular/router";
import {TaskService} from "../task/services/task.service";
import {Task} from "../../models/Task";
import {TaskFilters} from "../../models/TaskFilters";

@Component({
  selector: 'app-support',
  templateUrl: './support.component.html',
  styleUrls: ['./support.component.css']
})
export class SupportComponent implements OnInit {

  tasks: Task[] = [];
  tasksUpload: boolean = false;
  page: number = 1;
  pageSize: number = 30;
  maxSize: number = 100;
  filter: string = "";
  filterName = "name";

  constructor(private tokenService: TokenStorageService,
              private router: Router,
              private taskService: TaskService,
              private route: ActivatedRoute,) {}

  ngOnInit(): void {
    // this.taskService.getAllTasks(this.page, this.pageSize, this.filter, this.filterName).subscribe(data => {
    //   this.maxSize = data.total;
    //   this.tasks = data.tasks;
    //   this.tasksUpload = true;
    // });
  }

  addTask(task: Task | null = null) {
    let supportId = this.route.snapshot.paramMap.get('id');
    if (task) {
      this.router.navigate([`support/${supportId}/task/${task.id}`]);
    } else {
      this.router.navigate([`support/${supportId}/task`]);
    }

  }

  deleteTask(task: Task) {
    this.tasksUpload = false;
    this.taskService.deleteTask(task.id).subscribe(id => {
      console.log(id + ' task deleted!');
      window.location.reload();
    });
  }

  changePage(page: number) {
    // this.taskService.getAllTasks(page, 30, this.filter, this.filterName).subscribe(data => {
    //   this.maxSize = data.total;
    //   this.tasks = data.tasks;
    //   this.tasksUpload = true;
    //   window.scroll({
    //     top: 0,
    //     left: 0,
    //     behavior: 'smooth'
    //   });
    // });
  }

  protected readonly onsubmit = onsubmit;
  protected readonly taskFilters = TaskFilters;
  protected readonly Object = Object;
}
