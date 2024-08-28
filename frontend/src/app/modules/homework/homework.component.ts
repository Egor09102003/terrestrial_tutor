import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
    selector: 'app-homework',
    templateUrl: './homework.component.html',
    styleUrls: ['./homework.component.css'],
    changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HomeworkComponent { }
