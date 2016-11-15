webpackJsonp([6],{

/***/ 1371:
/***/ function(module, exports, __webpack_require__) {

"use strict";
"use strict";
var core_1 = __webpack_require__(1);
var HelpComponent = (function () {
    function HelpComponent() {
    }
    HelpComponent.prototype.ngOnInit = function () {
    };
    HelpComponent = __decorate([
        core_1.Component({
            selector: 'mpt-help',
            template: __webpack_require__(1520),
        }), 
        __metadata('design:paramtypes', [])
    ], HelpComponent);
    return HelpComponent;
}());
exports.HelpComponent = HelpComponent;


/***/ },

/***/ 1520:
/***/ function(module, exports) {

module.exports = "<h1>Help</h1>\n"

/***/ },

/***/ 863:
/***/ function(module, exports, __webpack_require__) {

"use strict";
"use strict";
var help_component_1 = __webpack_require__(1371);
var ng_module_1 = __webpack_require__(33);
var router_1 = __webpack_require__(32);
var routes = [
    { path: '', component: help_component_1.HelpComponent },
];
var HelpModule = (function () {
    function HelpModule() {
    }
    HelpModule = __decorate([
        ng_module_1.NgModule({
            imports: [
                router_1.RouterModule.forChild(routes)
            ],
            declarations: [
                help_component_1.HelpComponent,
            ],
            exports: [
                help_component_1.HelpComponent,
            ]
        }), 
        __metadata('design:paramtypes', [])
    ], HelpModule);
    return HelpModule;
}());
exports.HelpModule = HelpModule;


/***/ }

});
//# sourceMappingURL=6.map