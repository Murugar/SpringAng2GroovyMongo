webpackJsonp([3],{

/***/ 1021:
/***/ function(module, exports, __webpack_require__) {

"use strict";
"use strict";
function __export(m) {
    for (var p in m) if (!exports.hasOwnProperty(p)) exports[p] = m[p];
}
__export(__webpack_require__(1022));
exports.EMAIL_PATTERN = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*$";


/***/ },

/***/ 1022:
/***/ function(module, exports) {

"use strict";
"use strict";
var Validators = (function () {
    function Validators() {
    }
    Validators.match = function (c1) {
        return function (c2) {
            if (c1.value !== c2.value)
                return { matched: false };
        };
    };
    return Validators;
}());
exports.Validators = Validators;


/***/ },

/***/ 1372:
/***/ function(module, exports, __webpack_require__) {

"use strict";
"use strict";
var core_1 = __webpack_require__(1);
var router_1 = __webpack_require__(32);
var forms_1 = __webpack_require__(393);
var toastr = __webpack_require__(39);
var forms_2 = __webpack_require__(1021);
var user_service_1 = __webpack_require__(77);
var login_service_1 = __webpack_require__(67);
var SignupComponent = (function () {
    function SignupComponent(router, userService, loginService) {
        this.router = router;
        this.userService = userService;
        this.loginService = loginService;
    }
    SignupComponent.prototype.ngOnInit = function () {
        this.initForm();
    };
    SignupComponent.prototype.onSubmit = function (params) {
        var _this = this;
        this.userService.create(params)
            .mergeMap(function () {
            return _this.loginService.login(params.email, params.password);
        })
            .subscribe(function () {
            toastr.success('Registered Successfully');
            _this.router.navigate(['/home']);
        }, this.handleError);
    };
    SignupComponent.prototype.cancel = function () {
        this.router.navigate(['/login']);
    };
    SignupComponent.prototype.initForm = function () {
        this.name = new forms_1.FormControl('', forms_1.Validators.compose([
            forms_1.Validators.required,
            forms_1.Validators.minLength(4),
        ]));
        this.fname = new forms_1.FormControl('', forms_1.Validators.compose([
            forms_1.Validators.required,
            forms_1.Validators.minLength(10),
        ]));
        this.email = new forms_1.FormControl('', forms_1.Validators.compose([
            forms_1.Validators.required,
            forms_1.Validators.pattern(forms_2.EMAIL_PATTERN),
        ]));
        this.password = new forms_1.FormControl('', forms_1.Validators.compose([
            forms_1.Validators.required,
            forms_1.Validators.minLength(6),
        ]));
        this.passwordConfirmation = new forms_1.FormControl('', forms_1.Validators.compose([
            forms_1.Validators.required,
            forms_2.Validators.match(this.password),
        ]));
        this.myForm = new forms_1.FormGroup({
            name: this.name,
            fname: this.fname,
            email: this.email,
            password: this.password,
            passwordConfirmation: this.passwordConfirmation,
        });
    };
    SignupComponent.prototype.handleError = function (error) {
        switch (error.status) {
            case 400:
                if (error.json()['code'] === 'email_already_taken') {
                    toastr.error('Sign Up', 'This email is already taken.');
                }
        }
    };
    SignupComponent = __decorate([
        core_1.Component({
            selector: 'mpt-signup',
            styles: [__webpack_require__(1521)],
            template: __webpack_require__(1522),
        }), 
        __metadata('design:paramtypes', [(typeof (_a = typeof router_1.Router !== 'undefined' && router_1.Router) === 'function' && _a) || Object, (typeof (_b = typeof user_service_1.UserService !== 'undefined' && user_service_1.UserService) === 'function' && _b) || Object, (typeof (_c = typeof login_service_1.LoginService !== 'undefined' && login_service_1.LoginService) === 'function' && _c) || Object])
    ], SignupComponent);
    return SignupComponent;
    var _a, _b, _c;
}());
exports.SignupComponent = SignupComponent;


/***/ },

/***/ 1521:
/***/ function(module, exports) {

module.exports = ""

/***/ },

/***/ 1522:
/***/ function(module, exports) {

module.exports = "<h1 class=\"panel-heading\">Sign Up</h1>\n\n<form [formGroup]=\"myForm\" (submit)=\"onSubmit(myForm.value)\" novalidate>\n\n  <div class=\"form-group\"\n       [class.has-danger]=\"!name.valid && name.touched\">\n    <label class=\"col-form-label\" for=\"nameInput\">Name</label>\n    <input\n      type=\"text\"\n      class=\"form-control form-control-danger\"\n      id=\"nameInput\"\n      placeholder=\"Name\"\n      maxlength=\"30\"\n      formControlName=\"name\"/>\n    <div *ngIf=\"name.hasError('required') && name.touched\"\n         class=\"form-control-feedback\">Name is required\n    </div>\n    <div *ngIf=\"name.hasError('minlength') && name.touched\"\n         class=\"form-control-feedback\">Name must be at least 4 chars.\n    </div>\n  </div>\n\n  <div class=\"form-group\"\n       [class.has-danger]=\"!name.valid && name.touched\">\n    <label class=\"col-form-label\" for=\"fullInput\">Full Name</label>\n    <input\n      type=\"text\"\n      class=\"form-control form-control-danger\"\n      id=\"fullInput\"\n      placeholder=\"Full Name\"\n      maxlength=\"30\"\n      formControlName=\"fname\"/>\n    <div *ngIf=\"fname.hasError('required') && fname.touched\"\n         class=\"form-control-feedback\">Full Name is required\n    </div>\n    <div *ngIf=\"fname.hasError('minlength') && fname.touched\"\n         class=\"form-control-feedback\">Full Name must be at least 10 chars.\n    </div>\n  </div>\n\n  <div class=\"form-group\"\n       [class.has-danger]=\"!email.valid && email.touched\">\n    <label class=\"col-form-label\" for=\"emailInput\">Email</label>\n    <input\n      type=\"email\"\n      class=\"form-control form-control-danger\"\n      id=\"emailInput\"\n      placeholder=\"Email\"\n      maxlength=\"30\"\n      formControlName=\"email\"/>\n    <div *ngIf=\"email.hasError('required') && email.touched\"\n         class=\"form-control-feedback\">Email is required\n    </div>\n    <div *ngIf=\"email.hasError('pattern') && email.touched\"\n         class=\"form-control-feedback\">Invalid format\n    </div>\n  </div>\n\n  <div class=\"form-group\"\n       [class.has-danger]=\"!password.valid && password.touched\">\n    <label class=\"col-form-label\" for=\"passwordInput\">Password</label>\n    <input\n      type=\"password\"\n      class=\"form-control form-control-danger\"\n      id=\"passwordInput\"\n      placeholder=\"Password\"\n      maxlength=\"100\"\n      formControlName=\"password\"/>\n    <div *ngIf=\"password.hasError('required') && password.touched\"\n         class=\"form-control-feedback\">Password is required\n    </div>\n    <div *ngIf=\"password.hasError('minlength') && password.touched\"\n         class=\"form-control-feedback\">Password must be longer than 6 chars.\n    </div>\n  </div>\n\n  <div class=\"form-group\"\n       [class.has-danger]=\"!passwordConfirmation.valid && passwordConfirmation.touched\">\n    <label class=\"col-form-label\" for=\"passwordConfirmationInput\">Confirm\n      Password</label>\n    <input\n      type=\"password\"\n      class=\"form-control form-control-danger\"\n      id=\"passwordConfirmationInput\"\n      placeholder=\"Confirm Password\"\n      formControlName=\"passwordConfirmation\"/>\n    <div\n      *ngIf=\"passwordConfirmation.hasError('required') && passwordConfirmation.touched\"\n      class=\"form-control-feedback\">Password confirmation is required\n    </div>\n    <div\n      *ngIf=\"passwordConfirmation.hasError('matched') && passwordConfirmation.touched\"\n      class=\"form-control-feedback\">Doesn't match\n    </div>\n  </div>\n\n  <button type=\"submit\" class=\"btn btn-lg btn-primary\">Submit</button>\n  <button type=\"cancel\" class=\"btn btn-lg btn-danger\" (click)=\"cancel()\">Cancel</button>\n\n</form>\n\n"

/***/ },

/***/ 864:
/***/ function(module, exports, __webpack_require__) {

"use strict";
"use strict";
var common_1 = __webpack_require__(25);
var forms_1 = __webpack_require__(393);
var signup_component_1 = __webpack_require__(1372);
var ng_module_1 = __webpack_require__(33);
var router_1 = __webpack_require__(32);
var routes = [
    { path: '', component: signup_component_1.SignupComponent },
];
var SignupModule = (function () {
    function SignupModule() {
    }
    SignupModule = __decorate([
        ng_module_1.NgModule({
            imports: [
                common_1.CommonModule,
                router_1.RouterModule.forChild(routes),
                forms_1.FormsModule,
                forms_1.ReactiveFormsModule,
            ],
            declarations: [
                signup_component_1.SignupComponent,
            ],
            exports: [
                signup_component_1.SignupComponent,
            ]
        }), 
        __metadata('design:paramtypes', [])
    ], SignupModule);
    return SignupModule;
}());
exports.SignupModule = SignupModule;


/***/ }

});
//# sourceMappingURL=3.map