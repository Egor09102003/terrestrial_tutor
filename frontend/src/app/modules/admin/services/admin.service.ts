import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {environment} from "../../../../environments/environment";
import {EnvironmentService} from "../../../../environments/environment.service";


@Injectable({
  providedIn: 'root'
})
export class AdminService {

  constructor(private http: HttpClient,
              private apiService: EnvironmentService) {
  }

  private CHECK_API = this.apiService.apiUrl + 'check/';
  private ADMIN_API = this.apiService.apiUrl + 'admin/';
  private TUTOR_API = this.apiService.apiUrl + 'tutor/';

  getAllChecks(): Observable<any> {
    return this.http.get(this.CHECK_API + 'all');
  }

  deleteCheck(id: number): Observable<any> {
    return this.http.delete(this.CHECK_API + `close/${id}`);
  }

  findTutorsBySubject(subject: string | undefined): Observable<any> {
    return this.http.get(this.ADMIN_API + `subject/${subject}/find/tutors`);
  }

  getTutorPupilsBySubject(subject: string | undefined, id: number | undefined): Observable<any> {
    return this.http.get(this.TUTOR_API + `find/pupils/${subject}/${id}`);
  }

  findPupilsWithoutSubject(subject: string | undefined): Observable<any> {
    return this.http.get(this.ADMIN_API + `find/pupils/new/${subject}`);
  }

  findTutorsWithoutSubject(subject: string | undefined): Observable<any> {
    return this.http.get(this.ADMIN_API + `find/tutors/new/${subject}`);
  }

  addPupilsForTutor(pupilsIds: number[] | undefined, id: number | undefined, subject: string): Observable<any> {
    return this.http.post(this.ADMIN_API + `tutor/${id}/add/pupils`, pupilsIds, {
      params: {
        subject: subject,
      }
    });
  }

  addTutorsSubject(tutorsIds: number[] | undefined, subject: string | undefined): Observable<any> {
    return this.http.post(this.ADMIN_API + `tutor/add/subject/${subject}`, tutorsIds);
  }

}
