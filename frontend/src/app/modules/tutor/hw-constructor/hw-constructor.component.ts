import {Component, ElementRef, OnInit, QueryList, ViewChild, ViewChildren} from '@angular/core';
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
import {checkingTypes} from "../../../models/enums/CheckingTypes";
import {TaskCardComponent} from '../../task/card/task.card.component/task.card.component';
import {Pupil} from 'src/app/models/Pupil';
import {HomeworkService} from "../../homework/services/homework.service.";
import {ToastService} from "../../../components/toasts/toasts.service";

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
              private toastService: ToastService
              ) {
  }

  homework: Homework;
  hwForm: UntypedFormGroup;
  taskIds: number[] = [];
  pageLoaded: boolean = false;
  tutorId: number|null = null;
  homeworkUpdate: any;
  pupils: Pupil[] = [];
  selectedPupils: Pupil[] = [];

  ngOnInit(): void {
    let hwId = Number(this.route.snapshot.paramMap.get('hwId'));
    this.tutorId = Number(this.route.snapshot.paramMap.get('id'));
    this.homeworkService.getHomeworkById(hwId).subscribe(homework => {
      this.homework = homework;

      this.taskIds = Object.values(this.homework.taskChecking).sort(
        (checking1, checking2) =>
          checking1.orderIndex > checking2.orderIndex ? 1 : -1
      ).map((checking) => checking.task.id);

      this.tutorService.getAllCurrentPupils(this.homework.subject ?? '', this.tutorId ?? -1).subscribe(pupils => {
          this.pupils = pupils
          this.selectedPupils = this.pupils.filter(pupil => homework.pupilIds.indexOf(pupil.id) !== -1) ?? [];
        }
      )
      this.initForm();
    });
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
      this.homeworkService.saveHomework(this.homework).pipe(catchError((error: HttpErrorResponse) => {
        this.pageLoaded = true;
        this.toastService.show({
          message: "Ошибка при сохранении",
          classname: "bg-danger",
          delay: 5000
        });
        return throwError(() => error);
      })).subscribe(id => {
        this.pageLoaded = true;
      });
      this.toastService.show({
        message: "Домашнее задание сохранено",
        classname: "bg-success",
        delay: 5000
      });
    }
  }

  cancel() {
    clearInterval(this.homeworkUpdate);
    this.router.navigate([`tutor/${this.tutorId}`], {
      queryParams: {tab: this.route.snapshot.queryParamMap.get('tab') ?? 2},
      queryParamsHandling: 'merge'
    });
  }

  onDrop(event: CdkDragDrop<Task[]>) {
    moveItemInArray(this.taskIds, event.previousIndex, event.currentIndex);
    for (let i in this.taskIds) {
      this.homework.taskChecking[this.taskIds[i]].orderIndex = Number(i);
    }
  }

  deleteHomework() {
    this.tutorService.deleteHomeworkById(this.homework?.id).subscribe(() => {
      clearInterval(this.homeworkUpdate);
      this.router.navigate([`tutor/${this.tutorId}`], {
        queryParams: {tab: this.route.snapshot.queryParamMap.get('tab') ?? 2},
        queryParamsHandling: 'merge'
      });
      this.toastService.show({
        message: "Домашнее задание удалено",
        classname: "bg-success",
        delay: 5000
      });
    });

  }
  public readonly Object = Object;
  public readonly checkingTypes = checkingTypes;
  protected readonly Array = Array;
}
