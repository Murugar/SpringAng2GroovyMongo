webpackJsonp([1],{

/***/ 1023:
/***/ function(module, exports, __webpack_require__) {

"use strict";
"use strict";
var core_1 = __webpack_require__(1);
var http_error_handler_1 = __webpack_require__(66);
var RelatedUserListComponent = (function () {
    function RelatedUserListComponent(errorHandler) {
        this.errorHandler = errorHandler;
        this.users = [];
        this.noMoreUsers = false;
    }
    RelatedUserListComponent.prototype.ngOnInit = function () {
        this.list();
    };
    RelatedUserListComponent.prototype.loadMore = function () {
        var lastUser = this.users[this.users.length - 1];
        this.noMoreUsers = this.users.length != 0;
        if (!lastUser)
            return false;
        console.log(lastUser);
        this.list(lastUser.uid);
    };
    RelatedUserListComponent.prototype.list = function (maxId) {
        var _this = this;
        if (maxId === void 0) { maxId = null; }
        this.listProvider({ maxId: maxId, count: 5 })
            .subscribe(function (users1) {
            _this.users = _this.users.concat(users1);
            console.log(users1.length);
            if ((maxId) == null) {
                console.log('MaxID NULL  ' + users1.length);
                _this.noMoreUsers = users1.length < 5;
            }
            else {
                _this.noMoreUsers = users1.length === 0;
            }
            console.log(_this.noMoreUsers);
        }, function (e) { return _this.errorHandler.handle(e); });
    };
    __decorate([
        core_1.Input(), 
        __metadata('design:type', Function)
    ], RelatedUserListComponent.prototype, "listProvider", void 0);
    RelatedUserListComponent = __decorate([
        core_1.Component({
            selector: 'mpt-related-user-list',
            styles: [__webpack_require__(1296)],
            template: __webpack_require__(1297),
        }), 
        __metadata('design:paramtypes', [(typeof (_a = typeof http_error_handler_1.HttpErrorHandler !== 'undefined' && http_error_handler_1.HttpErrorHandler) === 'function' && _a) || Object])
    ], RelatedUserListComponent);
    return RelatedUserListComponent;
    var _a;
}());
exports.RelatedUserListComponent = RelatedUserListComponent;


/***/ },

/***/ 1024:
/***/ function(module, exports, __webpack_require__) {

"use strict";
"use strict";
var common_1 = __webpack_require__(25);
var router_1 = __webpack_require__(32);
var shared_module_1 = __webpack_require__(102);
var related_user_list_component_1 = __webpack_require__(1023);
var ng_module_1 = __webpack_require__(33);
var RelatedUserListModule = (function () {
    function RelatedUserListModule() {
    }
    RelatedUserListModule = __decorate([
        ng_module_1.NgModule({
            imports: [
                common_1.CommonModule,
                router_1.RouterModule,
                shared_module_1.SharedModule,
            ],
            declarations: [
                related_user_list_component_1.RelatedUserListComponent,
            ],
            exports: [
                related_user_list_component_1.RelatedUserListComponent,
            ]
        }), 
        __metadata('design:paramtypes', [])
    ], RelatedUserListModule);
    return RelatedUserListModule;
}());
exports.RelatedUserListModule = RelatedUserListModule;


/***/ },

/***/ 1296:
/***/ function(module, exports) {

module.exports = ".users li {\n  padding: 8px 0;\n  border-top: 1px solid #e8e8e8; }\n"

/***/ },

/***/ 1297:
/***/ function(module, exports) {

module.exports = "<ul class=\"users list-unstyled\">\n  <li *ngFor=\"let u of users\">\n    <mpt-gravatar [alt]=\"u.name\" [email]=\"u.email\" [size]=\"60\"></mpt-gravatar>\n    <a [routerLink]=\"['/users', u.uid]\">{{u.name}}</a>\n  </li>\n</ul>\n\n<button class=\"btn btn-warning moreBtn\" type=\"button\" (click)=\"loadMore()\"\n        [disabled]=\"noMoreUsers\">More\n</button>\n"

/***/ },

/***/ 1374:
/***/ function(module, exports, __webpack_require__) {

"use strict";
"use strict";
var core_1 = __webpack_require__(1);
var router_1 = __webpack_require__(32);
var user_service_1 = __webpack_require__(77);
var FollowingListComponent = (function () {
    function FollowingListComponent(userService, route) {
        this.userService = userService;
        this.route = route;
    }
    FollowingListComponent.prototype.ngOnInit = function () {
        var _this = this;
        this.route.params.subscribe(function (routeParams) {
            _this.userId = routeParams['id'];
            _this.listProvider = function (params) {
                return _this.userService.listFollowings(_this.userId, params);
            };
        });
    };
    FollowingListComponent = __decorate([
        core_1.Component({
            selector: 'mpt-following-list',
            template: __webpack_require__(1524),
        }), 
        __metadata('design:paramtypes', [(typeof (_a = typeof user_service_1.UserService !== 'undefined' && user_service_1.UserService) === 'function' && _a) || Object, (typeof (_b = typeof router_1.ActivatedRoute !== 'undefined' && router_1.ActivatedRoute) === 'function' && _b) || Object])
    ], FollowingListComponent);
    return FollowingListComponent;
    var _a, _b;
}());
exports.FollowingListComponent = FollowingListComponent;


/***/ },

/***/ 1524:
/***/ function(module, exports) {

module.exports = "<div class=\"row\">\n\n  <aside class=\"col-md-4\">\n    <mpt-user-stats [userId]=\"userId\"></mpt-user-stats>\n  </aside>\n\n  <div class=\"col-md-8\">\n    <h3>Following</h3>\n    <mpt-related-user-list [listProvider]=\"listProvider\"></mpt-related-user-list>\n  </div>\n\n</div>\n"

/***/ },

/***/ 866:
/***/ function(module, exports, __webpack_require__) {

"use strict";
"use strict";
var common_1 = __webpack_require__(25);
var components_1 = __webpack_require__(244);
var related_user_list_module_1 = __webpack_require__(1024);
var ng_module_1 = __webpack_require__(33);
var following_list_component_1 = __webpack_require__(1374);
var router_1 = __webpack_require__(32);
var routes = [
    { path: '', component: following_list_component_1.FollowingListComponent },
];
var FollowingListModule = (function () {
    function FollowingListModule() {
    }
    FollowingListModule = __decorate([
        ng_module_1.NgModule({
            imports: [
                common_1.CommonModule,
                router_1.RouterModule.forChild(routes),
                components_1.UserStatsModule,
                related_user_list_module_1.RelatedUserListModule
            ],
            declarations: [
                following_list_component_1.FollowingListComponent,
            ],
            exports: [
                following_list_component_1.FollowingListComponent,
            ]
        }), 
        __metadata('design:paramtypes', [])
    ], FollowingListModule);
    return FollowingListModule;
}());
exports.FollowingListModule = FollowingListModule;


/***/ }

});
//# sourceMappingURL=1.map