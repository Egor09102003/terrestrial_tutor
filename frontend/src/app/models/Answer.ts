import {AnswerStatuses} from "./enums/AnswerStatuses";
import {Task} from "./Task";

export interface Answer {
  id: number;
  answer: string;
  task: Task;
  points: number;
  status: AnswerStatuses;
}
