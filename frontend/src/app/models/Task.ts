import {Subject} from "./Subject";

export class Task {
  id: number
  name: string;
  checking: number;
  answerType: string;
  taskText: string;
  answers: string[];
  subject: string;
  level1: string;
  level2: string;
  table: string;
  files: string[];
  analysis: string;
  cost: number;
}
