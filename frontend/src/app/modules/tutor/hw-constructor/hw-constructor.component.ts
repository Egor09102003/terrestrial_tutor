import {Component, ElementRef, OnInit, QueryList, ViewChild, ViewChildren} from '@angular/core';
import {dataService} from "../services/data.service";
import {ActivatedRoute, Router} from "@angular/router";
import {TutorService} from "../services/tutor.service";
import {Homework} from "../../../models/Homework";
import {UntypedFormBuilder, UntypedFormGroup, Validators} from "@angular/forms";
import {Task} from "../../../models/Task";
import {CodemirrorComponent} from "@ctrl/ngx-codemirror";
import {throwError} from "rxjs";
import {TutorDataService} from "../storage/tutor.data.service";
import {CdkDrag, CdkDragDrop, CdkDropList, moveItemInArray} from "@angular/cdk/drag-drop";
import {catchError} from "rxjs/operators";
import {HttpErrorResponse} from "@angular/common/http";
import {checkingTypes} from "../../../models/CheckingTypes";
import { TaskCardComponent } from '../../task/card/task.card.component/task.card.component';

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
              private dataService: dataService,
              private router: Router,
              private fb: UntypedFormBuilder,
              private tutorDataService: TutorDataService,
              private route: ActivatedRoute,
              ) { }

  homework: Homework | null = null;
  //@ts-ignore
  hwForm: UntypedFormGroup;
  currentTasks: Task[]|null = null;
  pageLoaded: boolean = false;
  errorMessage = "";
  tutorId: number|null = null;

  ngOnInit(): void {
    let hwId = Number(this.route.snapshot.paramMap.get('hwId'));
    this.tutorId = Number(this.route.snapshot.paramMap.get('id'));
    this.tutorService.getHomework(hwId).subscribe(homework => {
      this.homework = homework;
      this.initFields();
      this.initForm();
    });

    setInterval(() => {
      this.saveHomework();
      this.tutorService.saveHomework(this.homework).subscribe();
    }, 60000);
  }

  initFields() {
    if (this.homework) {
      this.currentTasks = this.homework.tasks;
    }
  }

  initForm(): void {
    this.hwForm = this.fb.group( {
      name: [this.homework?.name, Validators.compose([Validators.required])],
      deadLine: [this.homework?.deadLine, /*Validators.compose([Validators.required])*/],
      targetTime: ['', /*Validators.compose([Validators.required])*/],
      tasks: [this.homework?.tasks, Validators.required]
    });
    if (this.homework != null)
      this.pageLoaded = true;
  }

  addTasks(): void {
    this.saveHomework();
    this.router.navigate([`/tutor/${this.tutorId}/constructor/${this.homework?.id}/hw/add/task`])
  }

  saveHomework() {
    this.homework!.name = this.hwForm.controls['name'].value;
    this.homework!.deadLine = this.hwForm.controls['deadLine'].value;
    this.homework!.targetTime = this.hwForm.controls['targetTime'].value;
    this.tutorDataService.setHomework(this.homework);
  }

  submit() {
    this.saveHomework();
    this.pageLoaded = false;
    if (this.homework) {
      this.tutorService.saveHomework(this.homework).pipe(catchError((error: HttpErrorResponse) => {
        this.errorMessage = "Ошибка при создании задания";
        this.pageLoaded = true;
        return throwError(error);
      })).subscribe(id => {
        this.pageLoaded = true;
        this.tutorDataService.setHomework(null);
        sessionStorage.removeItem("homeworkId");
        this.router.navigate([`/tutor/${this.tutorId}`]);
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
    this.homework!.tasksCheckingTypes[index] = type;
  }

  getChecking(index: number) {
    return this.homework?.tasksCheckingTypes[index] == 'AUTO' ? 'Авто' :
      this.homework?.tasksCheckingTypes[index] == 'INSTANCE' ? 'Моментальная' : 'Ручная';
  }

  addPupils() {
    this.saveHomework();
    this.router.navigate([`tutor/${this.tutorId}/constructor/${this.homework?.id}/add/pup`]);
  }

  onDrop(event: CdkDragDrop<Task[]>) {
    let tasks = this.homework?.tasks;
    let updatedCheckingMap: {[key: number]: string} = {};
    if (tasks) {
      moveItemInArray(tasks, event.previousIndex, event.currentIndex);
      if (this.homework) {
        this.homework.tasks = tasks;
      }
      if (this.homework?.tasksCheckingTypes) {
        for (let task of tasks) {
          updatedCheckingMap[task.id] = <string>this.homework?.tasksCheckingTypes[task.id];
        }
        this.homework.tasksCheckingTypes = updatedCheckingMap;
      }
    }
  }

  deleteHomework() {
    this.tutorService.deleteHomeworkById(this.homework?.id).subscribe();
    sessionStorage.setItem('tab', '2');
    this.router.navigate([`tutor/${this.tutorId}`]);
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

  public readonly Object = Object;
  public readonly checkingTypes = checkingTypes;
}
