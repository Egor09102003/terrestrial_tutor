import { Injectable } from '@angular/core';
import { HttpClient } from "@angular/common/http";
import {Observable} from "rxjs";
import {EnvironmentService} from "../../../../environments/environment.service";


@Injectable({
  providedIn: 'root'
})
export class PupilService {

  constructor(private http: HttpClient,
              private apiService: EnvironmentService) { }

  private PUPIL_API = this.apiService.apiUrl + 'pupil/';
  private PUPILS_API = this.apiService.apiUrl + 'pupils/';

  getCurrentUser(): Observable<any> {
    return this.http.get(this.PUPIL_API);
  }

  sendAnswers(answers: {[key: number]: string}, homeworkId: number, attempt: number) {
    return this.http.post(this.PUPIL_API + `homework/${homeworkId}/${attempt}`, answers);
  }

  getPupilById(id: string | null): Observable<any> {
    return this.http.get(this.PUPIL_API + `${id}`);
  }

  getHomeworkAnswers(homeworkId: number | null, idPupil: number | null, attempt: number | null): Observable<any> {
    return this.http.get(this.PUPIL_API + `homework/${homeworkId}/answers/${idPupil}/${attempt}`);
  }

  getCompletedHomeworks(pupilId: number) {
    return this.http.get(this.PUPIL_API + `${pupilId}/homework/completed`);
  }

  getPupilByIds(ids: number[], homeworkId: number): Observable<any> {
    return this.http.get(this.PUPILS_API + `check/list/${homeworkId}`, {
      params: {
        pupilIds: ids
      }
    });
  }

  getAll(): Observable<any> {
    return this.http.get(this.PUPILS_API);
  }

  public getActiveAttempt(hwId: number): Observable<any> {
    return this.http.get(this.PUPIL_API + `attempt`,
      {
        params: {
          homeworkId: hwId
        }
      }
    );
  }

}
