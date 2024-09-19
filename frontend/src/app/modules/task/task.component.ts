import {Component, OnInit} from '@angular/core';
import {
  AbstractControl,
  FormArray,
  FormControl, FormGroup,
  UntypedFormArray,
  UntypedFormBuilder,
  Validators
} from "@angular/forms";
import {Subject} from "../../models/Subject";
import {SubjectsService} from "../subjects/services/subjects.service";
import {SupportService} from "../support/services/support.service";
import {Task} from "../../models/Task";
import {ActivatedRoute, Router} from "@angular/router";
import {TaskService} from "./services/task.service";
import {answerTypes} from "../../models/AnswerTypes";
import {EnvironmentService} from 'src/environments/environment.service';
import {Lightbox} from "ngx-lightbox";
import {
  ClassicEditor,
  Link,
  Image,
  Font,
  Bold,
  Essentials,
  Italic,
  Mention,
  Paragraph,
  Undo,
  Table,
  TableToolbar,
  Base64UploadAdapter, ImageUpload, ImageInsert, ImageInsertViaUrl,
} from 'ckeditor5';
import {ChangeEvent} from "@ckeditor/ckeditor5-angular";
import Math from '@isaul32/ckeditor5-math/src/math';


@Component({
  selector: 'app-task',
  templateUrl: './task.component.html',
  styleUrls: ['./task.component.css'],
})
export class TaskComponent implements OnInit {

  taskForm: FormGroup;
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
  files: File[] = [];
  otherFiles: string[] = [];
  lightboxGallery: Array<any> = [];
  pageLoaded = false;
  public Editor = ClassicEditor;
  public config = {
    toolbar: [ 'undo', 'redo', 'fontColor', '|', 'bold', 'italic', 'insertTable', 'link', 'insertImage', ],
    plugins: [
      Bold, Essentials, Italic, Mention, Paragraph, Undo, Table, TableToolbar, Font, Link, Image
      , ImageUpload, ImageInsert,  ImageInsertViaUrl, Base64UploadAdapter,

    ],
    fontColor: {
      default: 'hsl(0, 0%, 0%)',
      colors: [
        {
          color: 'hsl(0, 0%, 0%)',
          label: 'default'
        }
      ]
    },
    link: {
      addTargetToExternalLinks: true
    },
  }

  constructor(private fb: UntypedFormBuilder,
              private subjectsService: SubjectsService,
              private supportService: SupportService,
              private taskService: TaskService,
              private router: Router,
              private route: ActivatedRoute,
              public env: EnvironmentService,
              private lightbox: Lightbox,
              ) {
  }

  ngOnInit(): void {
    let taskId = this.route.snapshot.paramMap.get('taskId');
    if (taskId) {
      this.taskService.getTaskById(taskId).subscribe(task => {
        this.task = task;
        if (task && 'files' in task) {
          for (let file of task.files) {
            if (file && this.isImage(file)) {
              this.lightboxGallery.push({
                src: this.env.filesPath + file,
                caption: file.substring(file.indexOf('$') + 1),
                thumb: this.env.filesPath + file,
              })
            } else if (file) {
              this.otherFiles.push(file);
            }
          }
        }
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
        taskAnswers.push(new FormControl(this.task.answers[i], Validators.compose([Validators.required])));
      }
    }

    this.taskForm = new FormGroup({
      taskName: new FormControl(this.task?.name ? this.task?.name : '', Validators.compose([Validators.required])),
      taskText: new FormControl(this.task?.taskText ? this.task?.taskText : '', Validators.compose([Validators.required])),
      taskAns: new FormArray(
        taskAnswers
        , Validators.compose([Validators.required,
        ])),
      selectedSubject: new FormControl(this.task?.subject ? this.task?.subject : 'Выберете предмет', Validators.compose([(subject) => {
        if (subject.value == 'Выберете предмет') {
          return {subjectNotSelected: false};
        } else {
          return null;
        }
      }])),
      answerType: new FormControl(this.task?.answerType ? this.task?.answerType : 'Выберете тип', Validators.compose([(answerType) => {
        if (answerType.value == 'Выберете тип') {
          return {answerType: false};
        } else {
          return null;
        }
      }])),
      level1: new FormControl(this.task?.level1 ? this.task?.level1 : '', Validators.compose([Validators.required])),
      level2: new FormControl(this.task?.level2 ? this.task?.level2 : ''),
      files: new FormControl(['']),
      table: new FormControl(this.task?.table ?? ''),
      analysis: new FormControl(this.task?.analysis ? this.task?.analysis : ''),
      cost: new FormControl(this.task?.cost ? this.task?.cost : 0),
    });

    if (this.task && 'answerType' in this.task && this.task.answerType === 'TABLE') {
      this.taskForm.controls['taskAns'].addValidators([this.validateTable]);
    }
  }

  getAllSubjects(): void {
    this.subjectsService.getAllSubjects().subscribe(data => {
      this.subjects = data;
    })
  }

  submit() {
    window.removeEventListener('beforeunload', this.reloadPage)
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
      table: this.taskForm.controls['table'].value,
      files: this.task?.files ?? [],
      analysis: this.formatLink(this.taskForm.controls['analysis'].value),
      cost: this.taskForm.controls['cost'].value,
    };

    if (this.task) {
      task.id = this.task.id;
    }
    let supportId = this.route.snapshot.paramMap.get('id');
    this.supportService.addTask(task).subscribe(taskId => {
      this.supportService.addFiles(this.files, taskId).subscribe(() => {
        if (!this.task) {
          this.router.navigate([`/support/${supportId}/task/${taskId}`])
        } else {
          window.location.reload();
        }
      });
    })
  }

  formatLink(link: string): string {
    if (link.includes('http') || link === '') {
      return link;
    }

    return 'https://' + link;
  }

  invalid(controlName: string) {
    return this.taskForm.controls[controlName].invalid && this.taskForm.controls[controlName].touched;
  }

  invalidAnswer(answer: any) {
    return answer.invalid && answer.touched;
  }

  saveFile(event: any) {
    let currentFiles: string[] = this.taskForm.controls['files'].value;
    let newFiles: File[] = Array.from(event.target.files);
    this.files = this.files?.concat(newFiles);
    currentFiles = currentFiles.concat(newFiles.map(file => file?.name));
    this.taskForm.controls['files'].setValue(currentFiles);
  }

  goBack(): void {
    window.history.back();
  }

  get taskAns() {
    return this.taskForm.get('taskAns') as UntypedFormArray;
  }

  addAns() {
    this.taskAns.push(new FormControl('', Validators.compose([Validators.required])));
  }

  deleteAns(index: number) {
    this.taskAns.removeAt(index);
  }

  typeChange(answerType: any) {
    if (answerType !== 'TABLE') {
      let fieldValue = '';
      if (this.taskForm.controls['answerType'].value !== 'TABLE') {
        fieldValue = this.taskForm.controls['taskAns'].value[0];
      }
      this.taskForm.controls['taskAns'] = new FormArray([
        new FormControl(fieldValue, Validators.compose([Validators.required])),
      ]);
      // this.taskForm.controls['taskAns'].removeValidators([this.validateTable]);
    } else {
      this.taskForm.controls['taskAns'].addValidators([this.validateTable]);
    }
    this.taskForm.controls['taskAns'].updateValueAndValidity();
    this.taskForm.updateValueAndValidity();
    this.pageUnload();
    this.taskForm.controls['answerType'].setValue(answerType);
  }

  isImage(file: string) {
    let fileExt = file.substring(file.lastIndexOf('.') + 1);
    return fileExt === 'png'
      || fileExt === 'jpg'
      || fileExt === 'svg';
  }

  deleteFile(file: string) {
    if (this.task && 'files' in this.task) {
      this.task.files.splice(this.task.files.indexOf(file), 1);
      this.taskForm.controls['files'].setValue(this.task.files);
    }
    if (this.lightboxGallery.includes(file)) {
      this.lightboxGallery.splice(this.lightboxGallery.indexOf(file), 1);
    }
    if (this.otherFiles.includes(file)) {
      this.otherFiles.splice(this.otherFiles.indexOf(file), 1);
    }
  }

  open(index: number): void {
    // open lightbox
    this.lightbox.open(this.lightboxGallery, index);
  }

  pageUnload() {
    this.taskForm.updateValueAndValidity();
    window.addEventListener('beforeunload', this.reloadPage);
  }

  reloadPage (event: Event) {
    event.preventDefault();
  }

  updateTable(table: string, property: string) {
    switch (property) {
      case 'table':
        this.taskForm.controls['table'].setValue(table);
        break;
      case 'answers':
        this.taskForm.controls['taskAns'] = new FormArray([
          new FormControl(table, Validators.compose([Validators.required])),
        ], this.validateTable);
        this.taskForm.controls['taskAns'].updateValueAndValidity();
        break;
      default:
        break;
    }
  }

  get answerTable() {
    return this.task?.answers[0] ?? '';
  }

  validateTable(taskAns: AbstractControl) {
    try {
      let decodedData = JSON.parse(taskAns.value);
      for (let row of decodedData) {
        for (let col of row) {
          if (col && col !== '') {
            return null;
          }
        }
      }
      return {taskAns: false};
    } catch (e) {
      return {taskAns: false};
    }
  }

  updateRTE({ editor }: ChangeEvent) {
    if (editor) {
      this.taskForm.controls['taskText'].setValue(editor.getData());
    }
  }

  protected readonly answerTypes = answerTypes;
}
