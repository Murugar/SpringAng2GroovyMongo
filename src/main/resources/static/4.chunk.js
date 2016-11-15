webpackJsonp([4],{

/***/ 1377:
/***/ function(module, exports, __webpack_require__) {

"use strict";
"use strict";
var core_1 = __webpack_require__(1);
var router_1 = __webpack_require__(32);
var UserShowComponent = (function () {
    function UserShowComponent(route) {
        this.route = route;
    }
    UserShowComponent.prototype.ngOnInit = function () {
        var _this = this;
        this.route.params.subscribe(function (params) {
            _this.userId = params['id'];
        });
    };
    UserShowComponent = __decorate([
        core_1.Component({
            selector: 'mpt-user-show',
            styles: [__webpack_require__(1528)],
            template: __webpack_require__(1529),
        }), 
        __metadata('design:paramtypes', [(typeof (_a = typeof router_1.ActivatedRoute !== 'undefined' && router_1.ActivatedRoute) === 'function' && _a) || Object])
    ], UserShowComponent);
    return UserShowComponent;
    var _a;
}());
exports.UserShowComponent = UserShowComponent;


/***/ },

/***/ 1528:
/***/ function(module, exports) {

module.exports = "aside {\n  margin-bottom: 4px;\n  position: relative; }\n\n.follow-btn {\n  position: absolute;\n  top: 0;\n  right: 0;\n  margin-right: 16px;\n  margin-top: 8px; }\n"

/***/ },

/***/ 1529:
/***/ function(module, exports) {

module.exports = "<div class=\"row\">\n\n  <aside class=\"col-md-4\">\n    <mpt-user-stats #us [userId]=\"userId\"\n                    [shownOnProfile]=\"true\"></mpt-user-stats>\n    <div class=\"follow-btn\">\n      <mpt-follow-btn [followerId]=\"userId\"\n                      (updated)=\"us.ngOnInit()\"></mpt-follow-btn>\n    </div>\n  </aside>\n\n  <div class=\"col-md-8\">\n    <mpt-micropost-list [userId]=\"userId\"></mpt-micropost-list>\n  </div>\n\n</div>\n"

/***/ },

/***/ 869:
/***/ function(module, exports, __webpack_require__) {

"use strict";
"use strict";
var common_1 = __webpack_require__(25);
var ng_module_1 = __webpack_require__(33);
var shared_module_1 = __webpack_require__(102);
var components_1 = __webpack_require__(244);
var user_show_component_1 = __webpack_require__(1377);
var router_1 = __webpack_require__(32);
var routes = [
    { path: '', component: user_show_component_1.UserShowComponent },
];
var UserShowModule = (function () {
    function UserShowModule() {
    }
    UserShowModule = __decorate([
        ng_module_1.NgModule({
            imports: [
                common_1.CommonModule,
                router_1.RouterModule.forChild(routes),
                shared_module_1.SharedModule,
                components_1.UserStatsModule,
                components_1.MicropostListModule,
            ],
            declarations: [
                user_show_component_1.UserShowComponent,
            ],
        }), 
        __metadata('design:paramtypes', [])
    ], UserShowModule);
    return UserShowModule;
}());
exports.UserShowModule = UserShowModule;


/***/ }

});
//# sourceMappingURL=4.map