import {CommonModule} from "@angular/common";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {NgModule} from "@angular/core/src/metadata/ng_module";
import {UserEditComponent} from "./user-edit.component";

import {RouterModule, Routes} from "@angular/router";
//import {ToastModule} from 'ng2-toastr/ng2-toastr';

const routes: Routes = [
    { path: '', component: UserEditComponent }
];

@NgModule( {
    imports: [
        CommonModule,
        RouterModule.forChild( routes ),
        FormsModule,
        ReactiveFormsModule,
       // ToastModule,
    ],
    declarations: [
        UserEditComponent,
    ],
    exports: [
        UserEditComponent,
    ]
})
export class UserEditModule {
}
