import {Component, OnInit} from "@angular/core";
import {FormGroup, FormControl, Validators} from "@angular/forms";
import {ActivatedRoute, Router} from "@angular/router";
import * as toastr from "toastr";
import {User} from "../../../core/domains";
import {UserService} from "../../../core/services/user.service";
import {EMAIL_PATTERN, Validators as AppValidators} from "../../../core/forms";
import * as load from "lodash-es";

//import { ToastsManager } from 'ng2-toastr/ng2-toastr';

@Component( {
    selector: 'mpt-user-edit',
    templateUrl: './user-edit.component.html',
})
export class UserEditComponent implements OnInit {

    myForm: FormGroup;
    name: FormControl;
    fname: FormControl;
    email: FormControl;
    password: FormControl;
    passwordConfirmation: FormControl;

    user: User;

    constructor( private route: ActivatedRoute,
        private router: Router,
      //  public toastr: ToastsManager,
        private userService: UserService ) {
    }

    ngOnInit(): void {
        this.route.data.subscribe( data => {
            this.user = data['profile'];
            this.initForm();
        });
    }

    onSubmit( params ) {
        this.passwordConfirmation.updateValueAndValidity( {});
        this.passwordConfirmation.markAsTouched();
        if ( !this.myForm.valid ) return;



        this.userService.updateMe( load.omitBy( params, load.isEmpty ) )
            .subscribe(() => {
                toastr.options.timeOut = 20;
                //toastr.options.progressBar = true;
                
                toastr.success('Successfully Updated.','Profile');
                
               // this.toastr.success('Success!', 'Updated User!');

                this.router.navigate( ['/home'] );

            }, this.handleError );
    }

    cancel() {
        this.router.navigate( ['/home'] );
    }

    private initForm() {
        this.name = new FormControl( this.user.name, Validators.compose( [
            Validators.required,
            Validators.minLength( 4 ),
        ] ) );
        this.fname = new FormControl( this.user.fname, Validators.compose( [
            Validators.required,
            Validators.minLength( 10 ),
        ] ) );
        this.email = new FormControl( this.user.email, Validators.compose( [
            Validators.required,
            Validators.pattern( EMAIL_PATTERN ),
        ] ) );
        this.password = new FormControl( '', Validators.compose( [
            Validators.minLength( 8 ),
        ] ) );
        this.passwordConfirmation = new FormControl( '', Validators.compose( [
            AppValidators.match( this.password ),
        ] ) );
        this.myForm = new FormGroup( {
            name: this.name,
            fname: this.fname,
            email: this.email,
            password: this.password,
            passwordConfirmation: this.passwordConfirmation,
        });
    }

    private handleError( error ) {
        switch ( error.status ) {
            case 400:
                if ( error.json()['code'] === 'email_already_taken' ) {
                    toastr.error( 'This email is already taken.', 'Profile Update' );
                }
        }
    }

}
