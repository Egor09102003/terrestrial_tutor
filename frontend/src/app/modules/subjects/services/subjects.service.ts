import { Injectable } from '@angular/core';
import { HttpClient } from "@angular/common/http";
import {Observable} from "rxjs";
import {EnvironmentService} from "../../../../environments/environment.service";


@Injectable({
  providedIn: 'root'
})
export class SubjectsService {

  constructor(private http: HttpClient,
              private apiService: EnvironmentService) { }

  private SUBJECTS_API = this.apiService.apiUrl + 'subjects/';

  getAllSubjects(): Observable<any> {
    return this.http.get(this.SUBJECTS_API);
  }

}
