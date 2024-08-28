import { Injectable } from '@angular/core';
import { Subject } from 'src/app/models/Subject';
import { Pupil } from 'src/app/models/Pupil';
import { Homework } from 'src/app/models/Homework';


@Injectable({
  providedIn: 'root'
})
export class PupilDataService {

  private pupil: Pupil | null = null;
  private currentSubject: string = '';
  private currentHomework: Homework | null = null;

  public getPupil(): Pupil|null {
    return this.pupil;
  }

  public setPupil(pupil: Pupil|null) {
    this.pupil = pupil;
  }

  public getCurrentSubject(): string {
    return this.currentSubject;
  }

  public setCurrentSubject(currentSubject: string) {
    this.currentSubject = currentSubject;
  }

  public getCurrentHomework(): Homework|null {
    return this.currentHomework;
  }

  public setCurrentHomework(currentHomework: Homework|null) {
    this.currentHomework = currentHomework;
  }

}
