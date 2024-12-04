import {ChangeDetectorRef, Component, ElementRef, OnInit, QueryList, ViewChild, ViewChildren} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {TutorService} from "../services/tutor.service";
import {Homework} from "../../../models/Homework";
import {UntypedFormBuilder, UntypedFormGroup, Validators} from "@angular/forms";
import {Task} from "../../../models/Task";
import {CodemirrorComponent} from "@ctrl/ngx-codemirror";
import {throwError} from "rxjs";
import {CdkDragDrop, CdkDropList, moveItemInArray} from "@angular/cdk/drag-drop";
import {catchError} from "rxjs/operators";
import {HttpErrorResponse} from "@angular/common/http";
import {checkingTypes} from "../../../models/CheckingTypes";
import {TaskCardComponent} from '../../task/card/task.card.component/task.card.component';
import {Pupil} from 'src/app/models/Pupil';
import {TaskChecking} from "../../../models/TaskChecking";
import {HomeworkService} from "../../homework/services/homework.service.";

@Component({
  selector: 'app-hw-constructor',
  templateUrl: './hw-constructor.component.html',
  styleUrls: ['./hw-constructor.component.css']
})
export class HwConstructorComponent implements OnInit {
  @ViewChild('codemirrorComponent') codemirror: CodemirrorComponent | undefined;
  @ViewChildren('taskCard') taskCollapse: QueryList<TaskCardComponent> ;
  @ViewChildren('cdkDrag') cdkDrag: QueryList<ElementRef> ;
  @ViewChildren('cdkDropList') cdkDropList: CdkDropList ;

  constructor(private tutorService: TutorService,
              private router: Router,
              private fb: UntypedFormBuilder,
              private route: ActivatedRoute,
              private homeworkService: HomeworkService,
              private cdr: ChangeDetectorRef
              ) { }

  homework: Homework;
  hwForm: UntypedFormGroup;
  currentTasks: number[] = [];
  pageLoaded: boolean = false;
  errorMessage = "";
  tutorId: number|null = null;
  state: boolean[] = [];
  homeworkUpdate: any;
  pupils: Pupil[] = [];
  selectedPupils: Pupil[] = [];

  ngOnInit(): void {
    let hwId = Number(this.route.snapshot.paramMap.get('hwId'));
    this.tutorId = Number(this.route.snapshot.paramMap.get('id'));
    this.homeworkService.getHomeworkById(hwId).subscribe(homework => {
      this.homework = homework;
      this.tutorService.getAllCurrentPupils(this.homework.subject ?? '', this.tutorId ?? -1).subscribe(pupils => {
          this.pupils = pupils
          this.selectedPupils = this.pupils.filter(pupil => homework.pupilIds.indexOf(pupil.id) !== -1) ?? [];
        }
      )
      this.initFields();
      this.initForm();
    });

    /*this.homeworkUpdate = setInterval(() => {
      this.saveHomework();
      this.tutorService.saveHomework(this.homework).subscribe();
    }, 10000);*/
  }

  initFields() {
    // this.currentTasks = Array.from(this.homework.taskChecking.keys());
  }

  initForm(): void {
    this.hwForm = this.fb.group( {
      name: [this.homework?.name, Validators.compose([Validators.required])],
      deadLine: [this.homework?.deadLine, /*Validators.compose([Validators.required])*/],
      targetTime: ['', /*Validators.compose([Validators.required])*/],
      tasks: [this.homework.taskChecking, Validators.required]
    });
    if (this.homework != null)
      this.pageLoaded = true;
  }

  addTasks(): void {
    this.getFromForm();
    this.router.navigate([`/tutor/${this.tutorId}/constructor/${this.homework?.id}/hw/add/task`], {
      queryParamsHandling: 'merge'
    })
  }

  getFromForm() {
    this.homework!.name = this.hwForm.controls['name'].value;
    this.homework!.deadLine = this.hwForm.controls['deadLine'].value;
    this.homework!.targetTime = this.hwForm.controls['targetTime'].value;
  }

  submit() {
    this.getFromForm();
    this.pageLoaded = false;
    if (this.homework) {
      this.tutorService.saveHomework(this.homework).pipe(catchError((error: HttpErrorResponse) => {
        this.errorMessage = "Ошибка при создании задания";
        this.pageLoaded = true;
        return throwError(error);
      })).subscribe(id => {
        this.pageLoaded = true;
        sessionStorage.removeItem("homeworkId");
        clearInterval(this.homeworkUpdate);
        // this.router.navigate([`tutor/${this.tutorId}`], {
        //   queryParams: {tab: this.route.snapshot.queryParamMap.get('tab') ?? 2},
        //   queryParamsHandling: 'merge'
        // });
      });
    }
  }

  checkImage(file: string): boolean {
    return file.endsWith('.jpg') || file.endsWith('.png') || file.endsWith('.jpeg');
  }

  codemirrorInit() {
    if (this.codemirror != undefined) {
      this.codemirror.codeMirror?.refresh();
    }
  }

  setChecking(index: number, type: string) {
    // if (
    //
    // )
    // this.homework.taskChecking.get(index).checkingType = type;
  }

  getChecking(index: number) {
    // return this.homework?.taskChecking[index].checkingType == 'AUTO' ? 'Авто' :
    //   this.homework?.taskChecking[index].checkingType == 'INSTANCE' ? 'Моментальная' : 'Ручная';
  }

  addPupils() {
    this.getFromForm();
    this.router.navigate([`tutor/${this.tutorId}/constructor/${this.homework?.id}/add/pup`], {
      queryParamsHandling: 'merge'
    });
  }

  onDrop(event: CdkDragDrop<Task[]>) {
    moveItemInArray(this.homework.taskIds, event.previousIndex, event.currentIndex);
  }

  deleteHomework() {
    this.tutorService.deleteHomeworkById(this.homework?.id).subscribe(() => {
      clearInterval(this.homeworkUpdate);
      this.router.navigate([`tutor/${this.tutorId}`], {
        queryParams: {tab: this.route.snapshot.queryParamMap.get('tab') ?? 2},
        queryParamsHandling: 'merge'
      });
    });

  }

  checkCollapse(i: number) {
    let currentDrag = this.cdkDrag.get(i)?.nativeElement;
    if (!this.taskCollapse.get(i)?.isCollapsed) {
      if (currentDrag?.draggable) {
        currentDrag.draggable = false;
      }
    } else {
      currentDrag.draggable = true;
    }
  }

  updateDrag(state: boolean, index: number) {
    this.state[index] = state;
  }

  ngOnDestroy() {
    clearInterval(this.homeworkUpdate);
  }

  public readonly Object = Object;
  public readonly checkingTypes = checkingTypes;
  protected readonly Array = Array;
}
