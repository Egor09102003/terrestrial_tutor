import { Injectable } from '@angular/core';
import { HttpClient } from "@angular/common/http";
import {EnvironmentService} from "../../../../environments/environment.service";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class TaskService {

  constructor(private http: HttpClient,
              private apiService: EnvironmentService) { }

  private TASK_API = this.apiService.apiUrl + 'tasks/';

  getAllTasks(page: number, size: number, filters: {}): Observable<any> {
    return this.http.get(this.TASK_API + 'all', {
      params: {...filters, ...{page: page, size: size}}
    });
  }

  getTasksBySubject(subject: any): Observable<any> {
    return this.http.get(this.TASK_API + `${subject}`);
  }

  getTaskById(id: any): Observable<any> {
    return this.http.get(this.apiService.apiUrl + `task/${id}`);
  }

  getFiles(fileName: string): Observable<any> {
    return this.http.get(this.TASK_API + `files/${fileName}`);
  }

  deleteTask(id: number) {
    return this.http.delete(this.TASK_API + `${id}/delete`);
  }

  getTaskByIds(ids: number[]): Observable<any> {
    return this.http.get(this.TASK_API, {
      params: {
        taskIds: ids,
      }
    })
  }
}
