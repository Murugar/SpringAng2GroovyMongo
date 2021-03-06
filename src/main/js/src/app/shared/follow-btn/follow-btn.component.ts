import {
  Component,
  OnChanges,
  EventEmitter,
  Output,
  Input,
  SimpleChanges
} from "@angular/core";
import {FollowBtnService} from "./follow-btn.service";
import {UserService} from "../../core/services/user.service";
import {HttpErrorHandler} from "../../core/services/http-error-handler";
import {User} from "../../core/domains";
import * as toastr from "toastr";

@Component({
  selector: 'mpt-follow-btn',
  templateUrl: './follow-btn.component.html',
})
export class FollowBtnComponent implements OnChanges {

  @Input() followerId: number;
  @Output() updated = new EventEmitter();

  canShowFollowBtn: boolean;
  canShowUnfollowBtn: boolean;
  busy: boolean = false;
  
  fuser: User;

  constructor(private followBtnService: FollowBtnService,
              private userService: UserService,
              private errorHandler: HttpErrorHandler) {
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (this.followerId) {
      this.loadCurrentStatus();
    }
  }

  follow() {
    this.busy = true;
    toastr.options.timeOut = 15;
    toastr.success(this.fuser.email, "Following")
    this.followBtnService.follow(this.followerId)
      .finally(() => this.busy = false)
      .subscribe(() => {
        this.canShowFollowBtn = !this.canShowFollowBtn;
        this.canShowUnfollowBtn = !this.canShowUnfollowBtn;
        this.updated.emit({});
      }, e => this.errorHandler.handle(e))
    ;
  }

  unfollow() {
    this.busy = true;
    toastr.options.timeOut = 15;
    toastr.success(this.fuser.email, "UnFollowing")
    this.followBtnService.unfollow(this.followerId)
      .finally(() => this.busy = false)
      .subscribe(() => {
        this.canShowFollowBtn = !this.canShowFollowBtn;
        this.canShowUnfollowBtn = !this.canShowUnfollowBtn;
        this.updated.emit({});
      }, e => this.errorHandler.handle(e))
    ;
  }

  loadCurrentStatus(): void {
    this.busy = true;
    
    console.log('Follow Button')
    
    this.userService.get(this.followerId)
      .finally(() => this.busy = false)
      .subscribe(user => {
        this.fuser = user;          
        this.canShowFollowBtn = this._canShowFollowBtn(user);
        this.canShowUnfollowBtn = this._canShowUnfollowBtn(user);
      }, e => this.errorHandler.handle(e))
    ;
  }

  private _canShowFollowBtn(user: User): boolean {
    if (user.isMyself === null) return false; // not signed in
    if (user.isMyself === true) return false; // myself
    if (user.userStats.followedByMe === true) return false; // already followed
    return true;
  }

  private _canShowUnfollowBtn(user: User): boolean {
    if (user.isMyself === null) return false; // not signed in
    if (user.isMyself === true) return false; // myself
    if (user.userStats.followedByMe === false) return false; // not followed yet
    return true;
  }
}
