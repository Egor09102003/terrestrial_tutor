import {Task} from "./Task";

export class TaskChecking implements Iterator<any> {
  id: number;
  homework: number;
  checkingType: string;
  task: Task

  next(): IteratorResult<Task> {
    return {
      done: true,
      value: this
    }
  }
}
