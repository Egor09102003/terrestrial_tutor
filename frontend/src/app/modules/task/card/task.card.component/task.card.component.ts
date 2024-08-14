import { Component, Input, Output, ViewChild, ViewEncapsulation } from '@angular/core';
import { UntypedFormControl } from '@angular/forms';
import {CodemirrorComponent} from "@ctrl/ngx-codemirror";
import { TaskSelect } from 'src/app/models/TaskSelect';
import {Task} from "../../../../models/Task";
import { answerTypes } from 'src/app/models/AnswerTypes';

@Component({
    selector: 'task-card',
    templateUrl: './task.card.component.html',
    styleUrl: './task.card.component.css',
    encapsulation: ViewEncapsulation.None
})
export class TaskCardComponent {
    @ViewChild('codemirrorComponent') codemirror: CodemirrorComponent | undefined;
    @Output() isCollapsed: boolean = true;
    @Input() task: Task;

    filterText = new UntypedFormControl('');
    protected readonly answerTypes = answerTypes;

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
 }
