import { Component, OnInit } from '@angular/core';
import {
  UntypedFormArray,
  UntypedFormBuilder, UntypedFormControl,
  UntypedFormGroup,
  Validators
} from "@angular/forms";
import {Subject} from "../../models/Subject";
import {SubjectsService} from "../subjects/services/subjects.service";
import {SupportService} from "../support/services/support.service";
import {Task} from "../../models/Task";
import {ActivatedRoute, Router} from "@angular/router";
import {TaskService} from "./services/task.service";
import {answerTypes} from "../../models/AnswerTypes";


@Component({
  selector: 'app-task',
  templateUrl: './task.component.html',
  styleUrls: ['./task.component.css']
})
export class TaskComponent implements OnInit {

  // @ts-ignore
  taskForm: UntypedFormGroup;
  subjects: Subject[] | undefined;
  options = {
    lineNumbers: true,
    theme: 'material',
    mode: 'python',
    matchBrackets: true,
    autoCloseBrackets: true,
    extraKeys: {'Ctrl-Space': 'autocomplete'}
  };
  task: Task | null = null;
  files: File[] | null = [];
  filesExist = false;
  pageLoaded = false;

  constructor(private fb: UntypedFormBuilder,
              private subjectsService: SubjectsService,
              private supportService: SupportService,
              private taskService: TaskService,
              private router: Router,
              private route: ActivatedRoute,
              ) {
  }

  ngOnInit(): void {
    let taskId = this.route.snapshot.paramMap.get('taskId');
    if (taskId) {
      this.taskService.getTaskById(taskId).subscribe(task => {
        this.task = task;
        this.createTaskForm();
        this.getAllSubjects();
        this.pageLoaded = true;
      });
    } else {
      this.createTaskForm();
      this.getAllSubjects();
      this.pageLoaded = true;
    }
  }

  private createTaskForm() {
    let taskAnswers = [];
    if (this.task) {
      for (let i = 0; i < this.task.answers.length; i++) {
        taskAnswers.push(this.fb.control(this.task.answers[i], Validators.compose([Validators.required])));
      }
    }
    let table: UntypedFormControl[][] = [];
    if (this.task?.table && JSON.parse(<string>this.task?.table)) {
      let currentTable = JSON.parse(<string>this.task?.table);
      for (let i = 0; i < currentTable.length; i++) {
        let row = [];
        for (let j = 0; j < currentTable[0].length; j++) {
          row.push(this.fb.control(currentTable[i][j]));
        }
        table.push(row);
      }
    } else {
      table = [[this.fb.control(''), this.fb.control(''), this.fb.control('')]];
    }

    let form = this.fb.group({
      taskName: [this.task?.name ? this.task?.name : '', Validators.compose([Validators.required])],
      taskText: [this.task?.taskText ? this.task?.taskText : '', Validators.compose([Validators.required])],
      taskAns: this.fb.array(
        taskAnswers
      , Validators.compose([Validators.required])),
      selectedSubject: [this.task?.subject ? this.task?.subject : 'Выберете предмет', Validators.compose([(subject) => {
        if (subject.value == 'Выберете предмет') {
          return {subjectNotSelected: false};
        } else {
          return null;
        }
      }])],
      answerType: [this.task?.answerType ? this.task?.answerType : 'Выберете тип', Validators.compose([(answerType) => {
        if (answerType.value == 'Выберете тип') {
          return {answerType: false};
        } else {
          return null;
        }
      }])],
      level1: [this.task?.level1 ? this.task?.level1 : '', Validators.compose([Validators.required])],
      level2: [this.task?.level2 ? this.task?.level2 : ''],
      files: [''],
      tableRows: [table.length],
      tableCols: [table.length > 0 ? table[0].length : ''],
      table: [
        table,
      ]
    });

    if (this.task && "id" in this.task) {
      this.taskService.getTaskFiles(this.task.id).subscribe(files => {
        const fileLoader = document.querySelector('input[type="file"]');
        const dataTransfer = new DataTransfer();
        if (files) {
          for (let file of files) {
            if ('fileName' in file) {
              let newFile: File = new File([file.file], file.fileName);
              let displayedFile: File = new File([file.file], file.fileName.substring(file.fileName.indexOf('.') + 1));
              this.files?.push(newFile);
              dataTransfer.items.add(displayedFile);
            }
          }
          this.filesExist = true;
          form.controls['files'].setValue(this.files);
          (<HTMLInputElement>fileLoader).files = dataTransfer.files;
        }
      })
    }
    this.taskForm = form;
  }

  getAllSubjects(): void {
    this.subjectsService.getAllSubjects().subscribe(data => {
      this.subjects = data;
    })
  }

  submit() {
    let task: Task = {
      id: 0,
      name: this.taskForm.controls['taskName'].value,
      answerType: this.taskForm.controls['answerType'].value,
      taskText: this.taskForm.controls['taskText'].value,
      answers: this.taskForm.controls['taskAns'].value,
      checking: 1,
      subject: this.taskForm.controls['selectedSubject'].value,
      level1: this.taskForm.controls['level1'].value,
      level2: this.taskForm.controls['level2'].value,
      table: this.tableToJson(),
      files: []
    };

    let files = this.taskForm.controls['files'].value;

    if (this.task) {
      task.id = this.task.id;
      task.files = this.task.files;
    }
    let supportId = this.route.snapshot.paramMap.get('id');
    this.supportService.addTask(task).subscribe(data => {
      this.supportService.addFiles(files, data).subscribe(() => this.router.navigate([`/support/${supportId}`]));
    })
  }

  invalid(controlName: string) {
    return this.taskForm.controls[controlName].invalid && this.taskForm.controls[controlName].touched;
  }

  invalidAnswer(answer: any) {
    return answer.invalid && answer.touched;
  }

  saveFile(event: any) {
    this.taskForm.controls['files'].setValue(event.target.files);
  }

  goBack(): void {
    window.history.back();
  }

  get taskAns() {
    return this.taskForm.get('taskAns') as UntypedFormArray;
  }

  addAns() {
    this.taskAns.push(this.fb.control('', Validators.compose([Validators.required])));
  }

  deleteAns(index: number) {
    this.taskAns.removeAt(index);
  }

  typeChange(answerType: any) {
    this.taskForm.controls['taskAns'] = (this.fb.array([
      this.fb.control('', Validators.compose([Validators.required]))
    ], Validators.compose([Validators.required])));
    this.taskForm.controls['answerType'].setValue(answerType);
  }

  get table() {
    return this.taskForm.get('table') as UntypedFormArray;
  }

  tableToJson() {
    let tableArray: any[][] = [];
    let table = this.taskForm.controls['table'].value;
    for (let i = 0; i < table.length; i++) {
      tableArray.push([])
      for (let col of table[i]) {
        tableArray[i].push(col.value);
      }
    }
    return JSON.stringify(tableArray);
  }

  renderTable() {
    let newTable: any[][] = [];
    let rows = this.taskForm.controls['tableRows'].value;
    let cols = this.taskForm.controls['tableCols'].value;
    for (let i = 0; i < rows; i++) {
      newTable.push([]);
      for (let j = 0; j < cols; j++) {
        newTable[i].push(this.fb.control(['']));
      }
    }
    this.taskForm.controls['table'].setValue(newTable);
  }

  protected readonly answerTypes = answerTypes;
}
