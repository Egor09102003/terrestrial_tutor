import {Task} from "./Task";

export class Homework {
  [key: string]: any;
  id?: number;
  name: string = '';
  targetTime: number = -1;
  pupilIds: number[] = [];
  deadLine?: Date;
  subject: string = '';
  public tasksCheckingTypes: { [key: number]: string } = {};
  tasks: Task[] = [];
  lastAttempt: number = 0;
}
