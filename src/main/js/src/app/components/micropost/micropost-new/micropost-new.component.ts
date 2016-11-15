import {Component, EventEmitter, Output} from "@angular/core";
import * as toastr from "toastr";
import {MicropostService} from "../../../core/services/micropost.service";
import {HttpErrorHandler} from "../../../core/services/http-error-handler";

@Component({
  selector: 'mpt-micropost-new',
  styleUrls: ['micropost-new.component.css'],
  templateUrl: 'micropost-new.component.html',
})
export class MicropostNewComponent {

  @Output() created = new EventEmitter();

  constructor(private micropostService: MicropostService,
              private errorHandler: HttpErrorHandler) {
  }

  create(content: HTMLInputElement, title: HTMLInputElement) {
    if (content.value === '') {
      toastr.warning('Post should have at least single character', 'Micropost');
      return;
    }
    
    if (title.value === '') {
        toastr.warning('Title should have a single character', 'Micropost');
        return;
      }

    this.micropostService.create(content.value, title.value)
      .subscribe(() => {
          
        toastr.options.timeOut = 20;
        toastr.success('Created!', 'MicroPost');
        content.value = '';
        title.value = '';
        this.created.emit({});
      }, e => this.errorHandler.handle(e))
    ;
  }

}
