import {inject, TestBed} from "@angular/core/testing";
import {
  Headers,
  ResponseOptions,
  Response,
  RequestMethod,
  HttpModule
} from "@angular/http";
import {
    fakeAsync,
    tick
  } from "@angular/core/testing";
  

import {MockBackend} from "@angular/http/testing";
import {LoginService} from "./login.service";
import {APP_TEST_HTTP_PROVIDERS} from "../../../testing";

describe('LoginService', () => {

  let loginService: LoginService;
  let backend: MockBackend;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpModule,
      ],
      providers: [
        APP_TEST_HTTP_PROVIDERS,
        LoginService,
      ],
    });
  });
  beforeEach(inject([LoginService, MockBackend], (..._) => {
    [loginService, backend] = _;
  }));

  describe( '.login', () => {



      it( 'can login',

        

              ( done ) => {


                  backend.connections.subscribe( conn => {
                      conn.mockRespond( new Response( new ResponseOptions( {
                          headers: new Headers( { 'x-auth-token': 'my jwt', 'x-user' : 'test1' }),
                      }) ) );
                      expect( conn.request.method ).toEqual( RequestMethod.Post );
                      expect( conn.request.url ).toEqual( '/api/login' );
                      expect( conn.request.json() ).toEqual( {
                          email: 'test@test.com',
                          password: 'secret',
                      });
                  });

                  loginService.login( 'test@test.com', 'secret' ).subscribe(() => {
                      expect( localStorage.getItem( 'jwt' ) ).toEqual( 'my jwt' );
                      expect( localStorage.getItem( 'user' ) ).toEqual( 'test1' );
                      done();
                  });

  });
}); 

  describe('.logout', () => {
    it('can logout', () => {
      localStorage.setItem('jwt', 'my jwt');
      localStorage.setItem('user', 'test1');
      loginService.signoff();
      expect(localStorage.getItem('jwt')).toBeFalsy();
      expect(localStorage.getItem('user')).toBeFalsy();
    });
  }); // .logout

  describe('.isSignedIn', () => {
    describe('when not signed in', () => {
      it('should be false', () => {
        expect(loginService.isSignedIn()).toBeFalsy();
      });
    });

    describe('when signed in', () => {
      beforeEach(() => localStorage.setItem('jwt', 'dummy'));
      beforeEach(() => localStorage.setItem('user', 'dummy'));
      it('should be true', () => {
        expect(loginService.isSignedIn()).toBeTruthy();
      });
    });
  }); // .isSignedIn

});

