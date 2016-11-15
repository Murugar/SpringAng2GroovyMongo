import {Observable} from "rxjs/Observable";
import {Injectable} from "@angular/core";
import {Response} from "@angular/http";
import {User, RelatedUser} from "../domains";
import {objToSearchParams} from "./helpers";
import {PageRequest, Page, UserParams} from "../dto";
import {JsonHttp} from "./";

const url = '/api/users';
const defaultPageRequest:PageRequest = {page: 1, size: 5};

@Injectable()
export class UserService {

  constructor(private http:JsonHttp) {
  }

  list(pageRequest:PageRequest = defaultPageRequest):Observable<Page<User>> {
    return this.http.get(url, {search: objToSearchParams(pageRequest)})
      .map(res => res.json())
      ;
  }

  get(id:number):Observable<User> {
    console.log(`${url}/${id}`)    
  
    return this.http.get(`${url}/${id}`)
      .map(res => res.json())
      ;
  }
  
  getMe(id:string):Observable<User> {
      return this.http.get(`${url}/${id}`)
        .map(res => res.json())
        ;
    }

  create(params:UserParams):Observable<Response> {
    return this.http.post(url, params);
  }
  
  remove(id:number):Observable<Response> {
      return this.http.delete(`${url}/remove/${id}`);
  }

  updateMe(userParam:UserParams):Observable<Response> {
    return this.http.patch(`${url}/me`, userParam)
      .do(resp => {
        localStorage.setItem('jwt', resp.headers.get('x-auth-token'));
      });
  }

  listFollowings(userId:number, params:{maxId:number, count:number}):Observable<RelatedUser[]> {
      
      
    console.log(`${url}/${userId}/followings`, {search: objToSearchParams(params)}) 
    return this.http.get(`${url}/${userId}/followings`, {search: objToSearchParams(params)})
      .map(res => res.json())
      ;
  }

  listFollowers(userId:number, params:{maxId:number, count:number}):Observable<RelatedUser[]> {
    console.log(`${url}/${userId}/followers`, {search: objToSearchParams(params)})     
  
    return this.http.get(`${url}/${userId}/followers`, {search: objToSearchParams(params)})
      .map(res => res.json())
      ;
  }

}
