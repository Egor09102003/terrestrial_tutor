import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {EnvironmentService} from "../../../../environments/environment.service";
import {Observable} from "rxjs";
import {Task} from "../../../models/Task";

@Injectable({
  providedIn: 'root'
})
export class TaskService {

  constructor(private http: HttpClient,
              private apiService: EnvironmentService) { }

  private TASK_API = this.apiService.apiUrl + 'tasks/';

  getAllTasks(): Observable<any> {
    return this.http.get(this.TASK_API + 'all');
  }

  getTasksBySubject(subject: any): Observable<any> {
    return this.http.get(this.TASK_API + `${subject}`);
  }

  getTaskById(id: any): Observable<any> {
    return this.http.get(this.apiService.apiUrl + `task/${id}`);
  }

  getTaskFiles(id: number): Observable<any> {
    return this.http.get(this.TASK_API + `${id}/files`);
  }

  deleteTask(id: number) {
    return this.http.delete(this.TASK_API + `${id}/delete`);
  }
}
