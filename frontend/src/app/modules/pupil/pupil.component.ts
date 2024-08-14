import { Component } from '@angular/core';
import {PupilService} from "./services/pupil.service";
import {Pupil} from "../../models/Pupil";
import { Router } from '@angular/router';
import { PupilDataService } from './services/pupil.data.service';

@Component({
  selector: 'app-pupil',
  templateUrl: './pupil.component.html',
  styleUrls: ['./pupil.component.css']
})
export class PupilComponent {

  pupil: Pupil | null = null;
  currentSubjects: string[] | null = null;

  constructor(private router: Router,
    private pupilDataService: PupilDataService,
    private pupilService: PupilService,) { }

  ngOnInit(): void {
    sessionStorage.clear();
    this.pupilDataService.setCurrentSubject(null);
    if (!this.pupilDataService.getPupil()) {
      this.pupilService.getCurrentUser().subscribe(pupil => {
        this.pupil = pupil;
        this.pupilDataService.setPupil(pupil);
        this.currentSubjects = pupil.subjects;
      })
    } else {
        this.pupil = this.pupilDataService.getPupil();
        if (this.pupil?.subjects) {
          this.currentSubjects = this.pupil?.subjects;
        }
    }

  }

  submit(subject: string) {
    this.pupilDataService.setCurrentSubject(subject);
    sessionStorage.setItem('currentSubject', JSON.stringify(subject));
    this.router.navigate([`/pupil/${this.pupil?.id}/homeworks`]);
  }

}
