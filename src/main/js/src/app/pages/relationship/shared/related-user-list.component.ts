import {Observable} from "rxjs/Observable";
import {Component, OnInit, Input} from "@angular/core";
import {RelatedUser} from "../../../core/domains";
import {HttpErrorHandler} from "../../../core/services/http-error-handler";

@Component({
  selector: 'mpt-related-user-list',
  styleUrls: ['./related-user-list.component.css'],
  templateUrl: './related-user-list.component.html',
})
export class RelatedUserListComponent implements OnInit {

  @Input() listProvider: (params: {maxId: number, count: number}) => Observable<RelatedUser[]>;

  users: RelatedUser[] = [];
  noMoreUsers: boolean = false;

  constructor(private errorHandler: HttpErrorHandler) {
  }

  ngOnInit(): any {
    this.list();
  }

  loadMore() {
    const lastUser = this.users[this.users.length - 1];
    
    this.noMoreUsers = this.users.length != 0;
    
    if (!lastUser) return false;
    console.log(lastUser)
    this.list(lastUser.uid);
  }

  private list(maxId = null) {
    this.listProvider({maxId: maxId, count: 5})
      .subscribe(users1 => {
          this.users = this.users.concat(users1);
          console.log(users1.length)
          if((maxId) == null)
          {
              console.log('MaxID NULL  ' + users1.length)    
          
              this.noMoreUsers = users1.length < 5;
          }
          else
          {
              this.noMoreUsers = users1.length === 0;
          }
          console.log(this.noMoreUsers)
        }, e => this.errorHandler.handle(e)
      )
    ;
  }

}
