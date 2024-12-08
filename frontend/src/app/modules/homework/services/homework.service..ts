import {HttpClient} from "@angular/common/http";
import {Injectable} from "@angular/core";
import {Observable} from "rxjs";
import {EnvironmentService} from "src/environments/environment.service";
import {Homework} from "../../../models/Homework";
import {Attempt} from "../../../models/Attempt";

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

  public saveHomeworkAnswers(answers: {[key: number]: string}, hwId: number) {
    return this.http.put(this.HOMEWORK_API + `save/${hwId}`, answers);
  }

  public finishHomework(answers: {[key: number]: string}, hwId: number) {
    return this.http.put(this.HOMEWORK_API + `finish/${hwId}`, answers);
  }

  public initAttempt(hwId: number, attempt: number): Observable<any> {
    return this.http.get(this.HOMEWORK_API + `${hwId}/init/${attempt}`);
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

  public updateAttemptStat(homeworkId: number, pupilId: number, updatedStatuses: Attempt): Observable<any> {
    return this.http.patch(this.HOMEWORK_API + `${homeworkId}/pupil/${pupilId}`, updatedStatuses);
  }

  public getAllHomeworks(): Observable<any> {
    return this.http.get(this.HOMEWORK_API + 'all');
  }

  public getHomeworkTutors(homeworkId: number): Observable<any> {
    return this.http.get(this.HOMEWORK_API + `${homeworkId}/tutors`);
  }

  public addHomeworkTutors(homeworkId: number, tutorIds: number[]) {
    return this.http.patch(this.HOMEWORK_API + `${homeworkId}/set/tutors`, tutorIds);
  }

  public saveHomework(homework: Homework | null): Observable<any> {
    return this.http.post(this.HOMEWORK_API + 'save', homework);
  }
}
