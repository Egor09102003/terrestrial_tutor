import { Injectable } from '@angular/core';
import { HttpClient } from "@angular/common/http";
import {EnvironmentService} from "../../../../environments/environment.service";
import {Observable} from "rxjs";
import {Task} from "../../../models/Task";

@Injectable({
  providedIn: 'root'
})
export class SupportService {

  constructor(private http: HttpClient,
              private apiService: EnvironmentService) { }

  private SUPPORT_API = this.apiService.apiUrl + 'support/';

  addTask(task: any): Observable<any> {
    return this.http.post(this.SUPPORT_API + 'add/task', task);
  }

  addFiles(files: any, id: number) {
    const formData = new FormData();
    for (let i = 0; i < files.length; i++) {
      formData.append('files', files[i]);
    }
    return this.http.post(this.SUPPORT_API + `add/file/${id}`, formData);
  }

}
