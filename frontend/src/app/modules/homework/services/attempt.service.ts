import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {EnvironmentService} from "../../../../environments/environment.service";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class AttemptService {

  constructor(
    private http: HttpClient,
    private apiService: EnvironmentService
  ) { }

  private PUPIL_API = this.apiService.apiUrl + 'pupil/';

  public saveAttempt(answers: {}, homeworkId?: number): Observable<any> {
    return this.http.post(this.PUPIL_API + 'attempt', answers, {
      params: {
        homeworkId: homeworkId ?? -1
      }
    });
  }

  public finishAttempt(answers: {}, homeworkId?: number): Observable<any> {
    return this.http.post(this.PUPIL_API + 'attempt/finish', answers, {
      params: {
        homeworkId: homeworkId ?? -1
      }
    });
  }

  public getLastFinishedAttempt(homeworkId?: number): Observable<any> {
    return this.http.get(this.PUPIL_API + 'attempt/finish', {
      params: {
        homeworkId: homeworkId ?? -1
      }
    });
  }

  public getAttemptByNumber(attemptNumber: number, homeworkId?: number): Observable<any> {
    return this.http.get(this.PUPIL_API + 'attempt/finish', {
      params: {
        homeworkId: homeworkId ?? -1,
        attemptNumber: attemptNumber
      }
    });
  }
}
