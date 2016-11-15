import {Component, OnInit} from "@angular/core";
import {Router} from "@angular/router";
import {FormGroup, FormControl, Validators} from "@angular/forms";
import * as toastr from "toastr";
import {
  EMAIL_PATTERN,
  Validators as AppValidators
} from "../../core/forms";
import {UserService} from "../../core/services/user.service";
import {LoginService} from "../../core/services/login.service";

@Component({
  selector: 'mpt-signup',
  styleUrls: ['./signup.component.css'],
  templateUrl: './signup.component.html',
})
export class SignupComponent implements OnInit {

  myForm: FormGroup;
  name: FormControl;
  fname: FormControl;
  email: FormControl;
  password: FormControl;
  passwordConfirmation: FormControl;

  constructor(private router: Router,
              private userService: UserService,
              private loginService: LoginService) {
  }

  ngOnInit(): void {
    this.initForm();
  }

  onSubmit(params) {
    this.userService.create(params)
      .mergeMap(() => {
        return this.loginService.login(params.email, params.password);
      })
      .subscribe(() => {
        toastr.success('Registered Successfully');    
        this.router.navigate(['/home']);
        
      }, this.handleError)
    ;
  }
  
  cancel() 
  {
      this.router.navigate(['/login']);
  }

  private initForm() {
    this.name = new FormControl('', Validators.compose([
      Validators.required,
      Validators.minLength(4),
    ]));
    this.fname = new FormControl('', Validators.compose([
                                                        Validators.required,
                                                        Validators.minLength(10),
                                                      ]));
    this.email = new FormControl('', Validators.compose([
      Validators.required,
      Validators.pattern(EMAIL_PATTERN),
    ]));
    this.password = new FormControl('', Validators.compose([
      Validators.required,
      Validators.minLength(6),
    ]));
    this.passwordConfirmation = new FormControl('', Validators.compose([
      Validators.required,
      AppValidators.match(this.password),
    ]));
    this.myForm = new FormGroup({
      name: this.name,
      fname: this.fname,
      email: this.email,
      password: this.password,
      passwordConfirmation: this.passwordConfirmation,
    });
  }

  private handleError(error) {
    switch (error.status) {
      case 400:
        if (error.json()['code'] === 'email_already_taken') {
          toastr.error('Sign Up', 'This email is already taken.');
        }
    }
  }

}
