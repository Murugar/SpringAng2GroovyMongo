webpackJsonp([5],{

/***/ 1376:
/***/ function(module, exports, __webpack_require__) {

"use strict";
"use strict";
var core_1 = __webpack_require__(1);
var router_1 = __webpack_require__(32);
var toastr = __webpack_require__(39);
var user_service_1 = __webpack_require__(77);
var http_error_handler_1 = __webpack_require__(66);
var UserListComponent = (function () {
    function UserListComponent(userService, errorHandler, route, router) {
        this.userService = userService;
        this.errorHandler = errorHandler;
        this.route = route;
        this.router = router;
    }
    UserListComponent.prototype.ngOnInit = function () {
        var _this = this;
        this.route.params.subscribe(function (params) {
            _this.page = +(params['page'] || 1);
            _this.list(_this.page);
        });
    };
    UserListComponent.prototype.onPageChanged = function (page) {
        this.router.navigate(['/users', { page: page }]);
    };
    UserListComponent.prototype.remove = function (userId) {
        var _this = this;
        if (!window.confirm('Are you sure?'))
            return;
        //const objId = mongoose.Types.ObjectId(userId)
        this.userService.remove(userId)
            .subscribe(function () {
        }, function (e) { return _this.errorHandler.handle(e); }, function () { return _this.users = _this.users.filter(function (p) { return p.uid !== userId; }); });
        toastr.options.timeOut = 20;
        toastr.success('Deleted!', 'User');
    };
    UserListComponent.prototype.list = function (page) {
        var _this = this;
        this.userService.list({ page: page, size: 5 })
            .subscribe(function (usersPage) {
            _this.users = usersPage.content;
            _this.totalPages = usersPage.totalPages;
        }, function (e) { return _this.errorHandler.handle(e); });
    };
    UserListComponent = __decorate([
        core_1.Component({
            selector: 'mpt-user-list',
            styles: [__webpack_require__(1526)],
            template: __webpack_require__(1527),
        }), 
        __metadata('design:paramtypes', [(typeof (_a = typeof user_service_1.UserService !== 'undefined' && user_service_1.UserService) === 'function' && _a) || Object, (typeof (_b = typeof http_error_handler_1.HttpErrorHandler !== 'undefined' && http_error_handler_1.HttpErrorHandler) === 'function' && _b) || Object, (typeof (_c = typeof router_1.ActivatedRoute !== 'undefined' && router_1.ActivatedRoute) === 'function' && _c) || Object, (typeof (_d = typeof router_1.Router !== 'undefined' && router_1.Router) === 'function' && _d) || Object])
    ], UserListComponent);
    return UserListComponent;
    var _a, _b, _c, _d;
}());
exports.UserListComponent = UserListComponent;


/***/ },

/***/ 1526:
/***/ function(module, exports) {

module.exports = ".users li {\n  padding: 8px 0;\n  border-top: 1px solid #e8e8e8; }\n"

/***/ },

/***/ 1527:
/***/ function(module, exports) {

module.exports = "<h1>All Users</h1>\n\n<ul class=\"users list-unstyled\">\n  <li *ngFor=\"let u of users\">\n    <mpt-gravatar [email]=\"u.email\" [size]=\"60\" [alt]=\"u.name\"></mpt-gravatar>\n    <a [routerLink]=\"['/users', u.uid]\">{{u.name}}</a>\n    <br/>\n    <br/>\n    <a href=\"#\" *ngIf=\"!u.me\"  class=\"delete text-danger\"\n         (click)=\"remove(u.uid); $event.preventDefault()\">Delete</a>\n  </li>\n</ul>\n\n<div class=\"clearfix\">\n  <mpt-pager class=\"pull-left\"\n             (pageChanged)=\"onPageChanged($event.page)\"\n             [totalPages]=\"totalPages\"\n             [currentPage]=\"page\"></mpt-pager>\n</div>\n"

/***/ },

/***/ 868:
/***/ function(module, exports, __webpack_require__) {

"use strict";
"use strict";
var common_1 = __webpack_require__(25);
var ng_module_1 = __webpack_require__(33);
var user_list_component_1 = __webpack_require__(1376);
var router_1 = __webpack_require__(32);
var shared_module_1 = __webpack_require__(102);
var routes = [
    { path: '', component: user_list_component_1.UserListComponent },
];
var UserListModule = (function () {
    function UserListModule() {
    }
    UserListModule = __decorate([
        ng_module_1.NgModule({
            imports: [
                common_1.CommonModule,
                router_1.RouterModule.forChild(routes),
                shared_module_1.SharedModule,
            ],
            declarations: [
                user_list_component_1.UserListComponent,
            ],
            exports: [
                user_list_component_1.UserListComponent,
            ]
        }), 
        __metadata('design:paramtypes', [])
    ], UserListModule);
    return UserListModule;
}());
exports.UserListModule = UserListModule;


/***/ }

});
//# sourceMappingURL=5.map