import {Component, OnInit} from "@angular/core";
import {ActivatedRoute, Router} from "@angular/router";
import {User} from "../../../core/domains";
import * as toastr from "toastr";
import {bson} from "bson";
import {UserService} from "../../../core/services/user.service";
import {HttpErrorHandler} from "../../../core/services/http-error-handler";
import * as mongoose from "mongoose";




@Component({
  selector: 'mpt-user-list',
  styleUrls: ['./user-list.component.css'],
  templateUrl: './user-list.component.html',
})
export class UserListComponent implements OnInit {

  users: User[];
  totalPages: number;
  page: number;

  

  constructor(private userService: UserService,
              private errorHandler: HttpErrorHandler,
              private route: ActivatedRoute,
              private router: Router) {
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.page = +(params['page'] || 1);
      this.list(this.page);
    });
  }

  onPageChanged(page: number) {
    this.router.navigate(['/users', {page: page}]);
  }

  remove(userId: number) {
      if (!window.confirm('Are you sure?')) return;
      
      //const objId = mongoose.Types.ObjectId(userId)
      
      this.userService.remove(userId)
      .subscribe(() => {
          }, e => this.errorHandler.handle(e),
          () => this.users = this.users.filter(p => p.uid !== userId)
        )
      ;
     toastr.options.timeOut = 20;  
     toastr.success('Deleted!', 'User');
   
    }
  
  private list(page: number) {
    this.userService.list({page: page, size: 5})
      .subscribe(usersPage => {
        this.users = usersPage.content;
        this.totalPages = usersPage.totalPages;
      }, e => this.errorHandler.handle(e))
    ;
  }

}
