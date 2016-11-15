import {Component, OnInit} from "@angular/core";
import {Router} from "@angular/router";
import {LoginService} from "../../core/services/login.service";

@Component({
  selector: 'mpt-header',
  styleUrls: ['header.component.css'],
  templateUrl: 'header.component.html',
})
export class HeaderComponent implements OnInit {

  isSignedIn: boolean;

  hasSignedIn: boolean;

  uname : string;

  constructor(private router: Router,
              private loginService: LoginService) {
     // console.log(this.loginService.semail);    
  
  }

  ngOnInit(): void {
    this.isSignedIn = this.loginService.isSignedIn();
    
    console.log('header');
    console.log(localStorage.getItem('user'));
    
    this.uname = localStorage.getItem('user')
    
    this.loginService.events.subscribe(() => {
      this.isSignedIn = this.loginService.isSignedIn();
    
      this.uname = localStorage.getItem('user')
    });
  }
  
  lname()
  {
      return this.uname;
  }

  signoff() {
    this.loginService.signoff();
    this.router.navigate(['/login']);
  }

}
