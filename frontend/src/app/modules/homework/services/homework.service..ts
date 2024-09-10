import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { HomeworkAnswers } from "src/app/models/HomeworkAnswers";
import { EnvironmentService } from "src/environments/environment.service";

@Injectable({
  providedIn: 'root'
})
export class HomeworkService {

  constructor(private http: HttpClient,
    private apiService: EnvironmentService) { }

  private HOMEWORKS_API = this.apiService.apiUrl + 'homeworks/';
  private HOMEWORK_API = this.apiService.apiUrl + 'homework/';

  public getHomeworksByPupilAndSubject(pupilId: number, subject: string): Observable<any> {
    return this.http.get(this.HOMEWORKS_API + `${pupilId}/${subject}`);
  }

  public getHomeworkByIdForPupil(hwId: number): Observable<any> {
    return this.http.get(this.HOMEWORK_API + `pupil/${hwId}`);
  }

  public saveHomework(answers: {[key: number]: string}, hwId: number) {
    return this.http.put(this.HOMEWORK_API + `save/${hwId}`, answers);
  }

  public finishHomework(answers: {[key: number]: string}, hwId: number) {
    return this.http.put(this.HOMEWORK_API + `finish/${hwId}`, answers);
  }

  public initLastAttempt(hwId: number): Observable<any> {
    return this.http.get(this.HOMEWORK_API + `${hwId}/init`);
  }

  public initAttempt(hwId: number, attempt: number): Observable<any> {
    return this.http.get(this.HOMEWORK_API + `${hwId}/init/${attempt}`);
  }

  public getHomeworkRightAnswers(homeworkId: number): Observable<any> {
    return this.http.get(this.HOMEWORK_API + `${homeworkId}/answers/right`);
  }

  public getHomeworkById(homeworkId: number): Observable<any> {
    return this.http.get(this.HOMEWORK_API + homeworkId);
  }

  public getLastAttemptAnswers(hwId: number, pupilId?: number): Observable<any> {
    return this.http.get(this.HOMEWORK_API + `${hwId}/answers`, {
      params: {
        pupilId: pupilId ?? ''
      }
    });
  }

  public getAttemptAnswers(hwId: number, attempt: number, pupilId?: number): Observable<any> {
    return this.http.get(this.HOMEWORK_API + `${hwId}/answers/${attempt}`, {
      params: {
        pupilId: pupilId ?? ''
      }
    });
  }

  public updateAttemptStat(homeworkId: number, pupilId: number, updatedStatuses: HomeworkAnswers): Observable<any> {
    return this.http.patch(this.HOMEWORK_API + `${homeworkId}/pupil/${pupilId}`, updatedStatuses);
  }
}
