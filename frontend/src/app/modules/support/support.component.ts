import { Component, OnInit } from '@angular/core';
import {TokenStorageService} from "../../security/token-storage.service";
import {ActivatedRoute, Router} from "@angular/router";
import {TaskService} from "../task/services/task.service";
import {Task} from "../../models/Task";

@Component({
  selector: 'app-support',
  templateUrl: './support.component.html',
  styleUrls: ['./support.component.css']
})
export class SupportComponent implements OnInit {

  tasks: Task[] = [];
  tasksUpload: boolean = false;

  constructor(private tokenService: TokenStorageService,
              private router: Router,
              private taskService: TaskService,
              private route: ActivatedRoute,) {}

  ngOnInit(): void {
    this.taskService.getAllTasks().subscribe(tasks => {
      this.tasks = tasks;
      this.tasksUpload = true;
    });
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
    this.taskService.deleteTask(task.id).subscribe(id => window.location.reload);
  }

  protected readonly onsubmit = onsubmit;
}
