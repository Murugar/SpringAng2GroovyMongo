import {Component} from "@angular/core";
import {Router} from "@angular/router";
import * as toastr from "toastr";
import {LoginService} from "../../core/services/login.service";

@Component({
  selector: 'mpt-login',
  styleUrls: ['./login.component.css'],
  templateUrl: './login.component.html',
})
export class LoginComponent {

  constructor(private router: Router,
              private loginService: LoginService) {
  }

  login(email, password) {
    this.loginService.login(email, password)
      .subscribe(() => {
   
       toastr.options.timeOut = 20;
       toastr.success(email, 'Welcome'); 
           
        this.router.navigate(['/home']);
       
      }, this.handleError)
    ;
   
  }

  handleError(error) {
    switch (error.status) {
      case 401:
        toastr.error('Email or Password is wrong.', 'Login');
    }
  }

}
