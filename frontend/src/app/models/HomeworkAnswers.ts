import {Homework} from "./Homework";

export interface HomeworkAnswers {
  answersStatuses: {[key: number]: Status};
  attemptCount: number;
}

export class Status {
  status: boolean
  currentAnswer: string
}