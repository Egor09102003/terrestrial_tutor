import { Component, OnInit } from '@angular/core';
import {PupilService} from "../../pupil/services/pupil.service";
import {Pupil} from "../../../models/Pupil";
import {PupilSelect} from "../../../models/PupilSelect";
import {Homework} from "../../../models/Homework";
import {ActivatedRoute, Router} from "@angular/router";
import {UntypedFormControl} from "@angular/forms";
import {TutorDataService} from "../storage/tutor.data.service";
import {TutorService} from "../services/tutor.service";

@Component({
  selector: 'app-pupils-add-homework',
  templateUrl: './pupils-add-homework.component.html',
  styleUrls: ['./pupils-add-homework.component.css']
})
export class PupilsAddHomeworkComponent implements OnInit {

  constructor(private pupilService: PupilService,
              private tutorDataService: TutorDataService,
              private router: Router,
              private tutorService: TutorService,
              private route: ActivatedRoute,
              ) { }

  allPupils: PupilSelect[] = [];
  currentPupils: number[] | undefined = [];
  homework: Homework | null = null;
  pageLoaded = false;
  filteredPupils: PupilSelect[] = [];
  filter = new UntypedFormControl('');

  ngOnInit(): void {
    this.homework = this.tutorDataService.getHomework();
    this.currentPupils = this.homework?.pupilIds;
    this.pupilService.getAllPupils().subscribe(pupils => {
      if (this.homework == null) {
        this.tutorService.getHomework(this.route.snapshot.paramMap.get('hwId')).subscribe(homework => {
          this.homework = homework;
          this.currentPupils = this.homework?.pupilIds;
          this.fillPupils(pupils);
        });
      } else {
        this.fillPupils(pupils);
      }
    })
    this.filter.valueChanges.subscribe((text) => {
      this.search(text);
    });
  }

  fillPupils(pupils: Pupil[]) {
    for (let i = 0; i < pupils.length; i++) {
      if (this.currentPupils && this.currentPupils.includes(pupils[i].id) &&
        this.homework &&
        pupils[i].subjects.includes(this.homework.subject)) {
        this.allPupils.push(new PupilSelect(pupils[i], true));
      } else if (this.homework &&
        pupils[i].subjects.includes(this.homework.subject)) {
        this.allPupils.push(new PupilSelect(pupils[i], false));
      }
      this.filteredPupils = this.allPupils;
    }
    this.pageLoaded = true;
  }

  getSelectedPupilsIds() {
    let selectedPupilsIds = [];
    let selectedPupils = [];
    for (let pupil of this.allPupils) {
      if (pupil.isSelected) {
        selectedPupilsIds.push(pupil.pupil.id);
        selectedPupils.push(pupil.pupil);
      }
    }
    return selectedPupilsIds;
  }

  search(text: any) {
    text = text.toLowerCase();
    this.filteredPupils = this.allPupils.filter(pupil => {
      return pupil.pupil.username.toLowerCase().includes(text) ||
        pupil.pupil.name.toLowerCase().includes(text) ||
        pupil.pupil.surname.toLowerCase().includes(text) ||
        pupil.pupil.patronymic.toLowerCase().includes(text);
    });
  }

  submit() {
    this.pageLoaded = false
    if (this.homework) {
      this.homework.pupilIds = this.getSelectedPupilsIds();
      this.tutorService.saveHomework(this.homework).subscribe(() => {
        let tutorId = this.route.snapshot.paramMap.get('id');
        this.pageLoaded = true;
        this.router.navigate([`tutor/${tutorId}/constructor/${this.homework?.id}`], {
          queryParamsHandling: 'merge'
        });
      });
    }
  }

}
