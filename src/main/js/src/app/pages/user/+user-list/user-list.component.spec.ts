import {Component, DebugElement} from "@angular/core";
import {By} from "@angular/platform-browser/src/dom/debug/by";
import {getDOM} from "@angular/platform-browser/src/dom/dom_adapter";
import {
  inject,
  fakeAsync,
  ComponentFixture,
  TestBed
} from "@angular/core/testing";
import {ResponseOptions, Response} from "@angular/http";
import {Router} from "@angular/router";
import {MockBackend} from "@angular/http/testing";
import {UserListComponent} from "./user-list.component";
import {RouterTestingModule} from "@angular/router/testing";
import {PagerComponent} from "../../../shared/pager/pager.component";
import {GravatarComponent} from "../../../shared/gravatar/gravatar.component";
import {CoreModule} from "../../../core/core.module";
import {UserListModule} from "./user-list.module";
import {APP_TEST_HTTP_PROVIDERS, advance} from "../../../../testing";
import {UserService} from "../../../core/services/user.service";

describe('UserListComponent', () => {

  @Component({
    template: `<router-outlet></router-outlet>`,
  })
  class TestComponent {
  }

  let cmpDebugElement: DebugElement;
  let pagerDebugElement: DebugElement;
  
  let userService: UserService;

  let router: Router;
  let backend: MockBackend;
  let fixture: ComponentFixture<any>;

  const dummyResponse = new Response(new ResponseOptions({
    body: JSON.stringify({
      content: [
        {id: 1, uid:1, email: 'test1@test.com', name: 'test1', fname: 'yyyyyyyyyy'},
        {id: 2, uid:2, email: 'test2@test.com', name: 'test2', fname: 'yyyyyyyyyy'},
        {id: 3, uid:3, email: 'test3@test.com', name: 'test3', fname: 'yyyyyyyyyy'},
      ],
      totalPages: 1,
      totalElements: 3,
    }),
  }));

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([
          {
            path: 'users',
            component: UserListComponent,
          },
        ]),
        CoreModule,
        UserListModule,
      ],
      providers: [
        APP_TEST_HTTP_PROVIDERS,
      ],
      declarations: [
        TestComponent,
      ]
    });
  });
  beforeEach(inject([UserService, Router, MockBackend], (..._) => {
    [userService, router, backend] = _;
    backend.connections.subscribe(conn => conn.mockRespond(dummyResponse));
  }));
  beforeEach(fakeAsync(() => {
    TestBed.compileComponents().then(() => {
      fixture = TestBed.createComponent(TestComponent);
      return router.navigate(['/users']).then(() => {
        cmpDebugElement = fixture.debugElement.query(By.directive(UserListComponent));
        pagerDebugElement = cmpDebugElement.query(By.directive(PagerComponent));
        fixture.detectChanges();
      });
    });
  }));

  it('can be shown', () => {
    expect(cmpDebugElement).toBeTruthy();
    expect(pagerDebugElement).toBeTruthy();
  });

  it('can list users', () => {
    const page: UserListComponent = cmpDebugElement.componentInstance;
    expect(page.users.length).toEqual(3);
    expect(page.totalPages).toEqual(1);

    const el = cmpDebugElement.nativeElement;
    expect(getDOM().querySelectorAll(el, 'li>a')[0].innerText).toEqual('test1');
    expect(getDOM().querySelectorAll(el, 'li>a')[1].innerText).toEqual('Delete');
    expect(getDOM().querySelectorAll(el, 'li>a')[2].innerText).toEqual('test2');
    expect(getDOM().querySelectorAll(el, 'li>a')[3].innerText).toEqual('Delete');
    expect(getDOM().querySelectorAll(el, 'li>a')[4].innerText).toEqual('test3');
    expect(getDOM().querySelectorAll(el, 'li>a')[5].innerText).toEqual('Delete');
    
    const deleteLinks = getDOM().querySelectorAll(el, '.delete');
    expect(deleteLinks[0]).toBeTruthy();
    expect(deleteLinks[1]).toBeTruthy();
    expect(deleteLinks[2]).toBeTruthy();

    const gravatarDebugElement = cmpDebugElement.query(By.directive(GravatarComponent));
    expect(gravatarDebugElement).toBeTruthy();
    expect(gravatarDebugElement.componentInstance.email).toEqual('test1@test.com');
    expect(gravatarDebugElement.componentInstance.alt).toEqual('test1');

    const userShowLink = cmpDebugElement.query(By.css('li>a')).nativeElement;
    expect(userShowLink.getAttribute('href')).toEqual('/users/1');

    const pager: PagerComponent = pagerDebugElement.componentInstance;
    expect(pager.totalPages).toEqual(1);
  });

  it('list another page when page was changed', fakeAsync(() => {
    pagerDebugElement.triggerEventHandler('pageChanged', {page: 2});
    advance(fixture);
    cmpDebugElement = fixture.debugElement.query(By.directive(UserListComponent));
    const cmp: UserListComponent = cmpDebugElement.componentInstance;
    expect(cmp.page).toEqual(2);
  }));
  
  it('deletes user when confirmed', () => {
      const page: UserListComponent = cmpDebugElement.componentInstance;
      const deleteLink = getDOM().querySelector(cmpDebugElement.nativeElement, '.delete');
      spyOn(window, 'confirm').and.returnValue(true);
      deleteLink.click();
      expect(page.users.length).toEqual(2);
    });
  
  it('does not remove user when not confirmed', () => {
      const deleteLink = getDOM().querySelector(cmpDebugElement.nativeElement, '.delete');
      spyOn(window, 'confirm').and.returnValue(false);
      spyOn(userService, 'remove');
      deleteLink.click();
      expect(userService.remove).not.toHaveBeenCalled();
  }); 
  
  
  

});
