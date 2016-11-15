import {inject, TestBed} from "@angular/core/testing";
import {Router} from "@angular/router";
import {HttpErrorHandler} from "./http-error-handler";
import {LoginService} from "./login.service";
import {CoreModule} from "../core.module";
import {APP_TEST_HTTP_PROVIDERS} from "../../../testing";

describe('HttpErrorHandler', () => {

  let errorHandler: HttpErrorHandler;
  let loginService: LoginService;
  let router: Router;

  class MockRouter {
    navigate() {
    }
  }

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        CoreModule,
      ],
      providers: [
        {
          provide: Router,
          useClass: MockRouter,
        },
        APP_TEST_HTTP_PROVIDERS,
      ],
    });
  });

  beforeEach(inject([HttpErrorHandler, LoginService, Router], (..._) => {
    [errorHandler, loginService, router] = _;
    spyOn(loginService, 'signoff');
    spyOn(router, 'navigate');
  }));

  describe('.handle', () => {
    it('handles 401 response', () => {
      errorHandler.handle({status: 401});
      expect(loginService.signoff).toHaveBeenCalled();
      expect(router.navigate).toHaveBeenCalledWith(['login']);
    });

    it('does not handle other errors', () => {
      errorHandler.handle({status: 400});
      expect(loginService.signoff).not.toHaveBeenCalled();
      expect(router.navigate).not.toHaveBeenCalled();
    });
  }); // .handle

});
