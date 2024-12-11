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

  private ATTEMPT_API = this.apiService.apiUrl + 'attempt/';

  public saveAttempt(answers: {}, homeworkId?: number): Observable<any> {
    return this.http.post(this.ATTEMPT_API, answers, {
      params: {
        homeworkId: homeworkId ?? -1
      }
    });
  }

  public getActiveAttempt(hwId: number): Observable<any> {
    return this.http.get(this.ATTEMPT_API,
      {
        params: {
          homeworkId: hwId
        }
      }
    );
  }

  public finishAttempt(answers: {}, homeworkId?: number): Observable<any> {
    return this.http.post(this.ATTEMPT_API + 'finish', answers, {
      params: {
        homeworkId: homeworkId ?? -1
      }
    });
  }

  public getLastFinishedAttempt(homeworkId?: number): Observable<any> {
    return this.http.get(this.ATTEMPT_API + 'finish', {
      params: {
        homeworkId: homeworkId ?? -1
      }
    });
  }

  public getAttemptByNumber(attemptNumber: number, homeworkId?: number): Observable<any> {
    return this.http.get(this.ATTEMPT_API + 'finish', {
      params: {
        homeworkId: homeworkId ?? -1,
        attemptNumber: attemptNumber
      }
    });
  }
}
