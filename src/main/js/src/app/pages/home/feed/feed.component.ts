import {Component, EventEmitter, Output, OnInit} from "@angular/core";
import {FeedService} from "./feed.service";
import {Micropost} from "../../../core/domains";
import * as toastr from "toastr";
import {MicropostService} from "../../../core/services/micropost.service";
import {HttpErrorHandler} from "../../../core/services/http-error-handler";

@Component({
  selector: 'mpt-feed',
  styleUrls: ['./feed.component.css'],
  templateUrl: './feed.component.html',
})
export class FeedComponent implements OnInit {

  feed: Micropost[] = [];

  @Output() deleted = new EventEmitter();

  constructor(private micropostService: MicropostService,
              private feedService: FeedService,
              private errorHandler: HttpErrorHandler) {
  }

  ngOnInit(): void {
      
  
    this.list();
  //  console.log( this.list());
    console.log( this.feed);
  }

  list(): void {
    this.feedService.showFeed()
      .subscribe(feed => this.feed = feed,
        e => this.errorHandler.handle(e))
    ;
    
 
  }

  show()
  {
      toastr.options.timeOut = 20;  
      toastr.success('No Posts', 'MicroPost');
  }
  
  delete(id: number) {
    if (!window.confirm('Are you sure?')) return;
    this.micropostService.delete(id)
      .subscribe(() => {
        this.list();
        this.deleted.emit({});
      }, e => this.errorHandler.handle(e))
    ;
    toastr.options.timeOut = 20;
    toastr.success('Deleted!', 'MicroPost');
  }

}
