import { CommonModule } from '@angular/common';
import { Component, type OnInit } from '@angular/core';
import { StatisticService } from '../../homework/services/statistic.service';
import { ActivatedRoute } from '@angular/router';
import { Homework } from 'src/app/models/Homework';
import { Task } from 'src/app/models/Task';
import { HomeworkAnswers } from 'src/app/models/HomeworkAnswers';
import { HomeworkService } from '../../homework/services/homework.service.';

@Component({
    selector: 'app-check.homeworks',
    templateUrl: './check.homeworks.component.html',
    styleUrl: './check.homeworks.component.css',
})
export class CheckHomeworksComponent implements OnInit {

    homework: Homework;
    pupilAnswers: HomeworkAnswers;
    tasks: Task[] = [];
    statistic = {
        percent: 0,
        points: 0,
        pointsMax: 0,
    };
    pageLoaded = false;
    attempts: number[] = [];
    rightAnswers: {[key: number]: string};
    pupilId: number;

    constructor(
        private route: ActivatedRoute,
        private statisticService: StatisticService,
        private homeworkService: HomeworkService,
    ){}

    ngOnInit(): void {
        let homeworkId = Number(this.route.snapshot.queryParamMap.get('homework')) ?? -1;
        this.pupilId = Number(this.route.snapshot.queryParamMap.get('pupil')) ?? -1;
        this.statisticService.init(homeworkId, this.pupilId);
        this.statisticService.pageLoaded.subscribe(pageLoaded => {
            this.homework = this.statisticService.homework;
            this.pupilAnswers = this.statisticService.pupilAnswers;
            this.tasks = this.statisticService.tasks;
            this.attempts = this.statisticService.attempts;
            this.rightAnswers = this.statisticService.rightAnswers;
            this.pageLoaded = true;
        });
    }

    changeStatus(task: Task, data: {status?: string, points?: any}) {
        if (data.status) {
            this.pupilAnswers.answersStatuses[task.id].status = data.status;
        }
        if (data.points) {
            this.pupilAnswers.answersStatuses[task.id].points = Number(data.points.target.value) ?? 0;
        }
    }

    updateStatus() {
        this.homeworkService.updateAttemptStat(this.homework.id ?? -1, this.pupilId, this.pupilAnswers).subscribe(pupilAnswers => this.pupilAnswers = pupilAnswers);
    }

    test(tasks: Task[]) {
        this.tasks = tasks;
        console.log(tasks);
    }
    test1(ans: HomeworkAnswers) {
        this.pupilAnswers = ans;
        console.log(ans);
    }
}
