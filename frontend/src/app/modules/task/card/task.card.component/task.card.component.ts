import { Component, EventEmitter, Input, Output, ViewChild, ViewEncapsulation } from '@angular/core';
import { UntypedFormControl } from '@angular/forms';
import {CodemirrorComponent} from "@ctrl/ngx-codemirror";
import { TaskSelect } from 'src/app/models/TaskSelect';
import {Task} from "../../../../models/Task";
import { answerTypes } from 'src/app/models/AnswerTypes';
import { EnvironmentService } from 'src/environments/environment.service';
import { ReturnStatement } from '@angular/compiler';

@Component({
    selector: 'task-card',
    templateUrl: './task.card.component.html',
    styleUrl: './task.card.component.css',
    encapsulation: ViewEncapsulation.None
})
export class TaskCardComponent {
    @ViewChild('codemirrorComponent') codemirror: CodemirrorComponent | undefined;
    @Output() state = new EventEmitter<boolean>;
    isCollapsed: boolean = true;
    @Input() task: Task;

    filterText = new UntypedFormControl('');
    protected readonly answerTypes = answerTypes;

    constructor(
        public env: EnvironmentService,
    ) {

    }

    ngOnInit() {

    }

    checkImage(file: string): boolean {
        return file.endsWith('.jpg') || file.endsWith('.png') || file.endsWith('.jpeg');
    }
    
    codemirrorInit() {
        if (this.codemirror != undefined) {
            this.codemirror.codeMirror?.refresh();
        }
    }

    returnState() {
        this.state.emit(this.isCollapsed);
    }
 }
