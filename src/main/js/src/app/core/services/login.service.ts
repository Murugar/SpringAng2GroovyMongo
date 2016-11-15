import {Observable} from "rxjs/Observable";
import {Injectable} from "@angular/core";
import {Response} from "@angular/http";
import {Subject} from "rxjs/Rx";
import {JsonHttp} from "./";
import * as toastr from "toastr";


@Injectable()
export class LoginService {

  private authEvents: Subject<AuthEvent>;
  
  semail : string;

  constructor(private http: JsonHttp) {
    this.authEvents = new Subject<AuthEvent>();
  }

  login(email: string, password: string): Observable<Response> {
    
    const body = {
      email: email,
      password: password,
    };
    return this.http.post('/api/login', body).do(resp => {
   
      localStorage.setItem('jwt', resp.headers.get('x-auth-token'));
      localStorage.setItem('user', resp.headers.get('x-user'));
      this.authEvents.next(new DidLogin());
    });
  }

  signoff(): void {
    localStorage.removeItem('jwt');
    localStorage.removeItem('user');
    this.authEvents.next(new DidLogout());
    toastr.success('Bye'); 
  }

  isSignedIn(): boolean {
      
    console.log('Login Service Is Signed');
    console.log(localStorage.getItem('user'));  
    console.log(localStorage.getItem('jwt'));
    return localStorage.getItem('jwt') !== null;
  }

  get events(): Observable<AuthEvent> {
    return this.authEvents;
  }

}

export class DidLogin {
}
export class DidLogout {
}

export type AuthEvent = DidLogin | DidLogout;

